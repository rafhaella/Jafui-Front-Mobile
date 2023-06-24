package com.mobile.jafui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.mobile.jafui.api.RestServiceGenerator;
import com.mobile.jafui.api.UserService;
import com.mobile.jafui.databinding.ActivityHomeBinding;
import com.mobile.jafui.R;
import com.mobile.jafui.entidades.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private String userEmail;
    private ActivityResultLauncher<Intent> launcher;
    private static final int REQUEST_CODE_PROFILE = 1;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userEmail = getIntent().getStringExtra("email");

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.btnAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreatePlaceActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            User updatedUser = (User) result.getData().getSerializableExtra("user");
                            if (updatedUser != null) {
                                currentUser = updatedUser;
                                updateUserDataInNav(currentUser);
                            }
                        }
                    }
                });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View navHeaderView = navigationView.getHeaderView(0);
        Button btnEditProfile = navHeaderView.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserByEmail(userEmail, new UserProfileCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
                        intent.putExtra("email", userEmail);
                        intent.putExtra("user", user);
                        launcher.launch(intent);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(HomeActivity.this, "Erro ao obter os dados do usuário", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Obter dados do usuário ao iniciar a atividade
        getUserByEmail(userEmail, new UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                updateUserDataInNav(currentUser);
            }

            @Override
            public void onFailure() {
                Toast.makeText(HomeActivity.this, "Erro ao obter os dados do usuário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface UserProfileCallback {
        void onSuccess(User user);
        void onFailure();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent editIntent = new Intent(HomeActivity.this, MainActivity.class);
            Toast.makeText(HomeActivity.this, "Usuário deslogado com sucesso", Toast.LENGTH_SHORT).show();
            startActivity(editIntent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void getUserByEmail(String email, final UserProfileCallback callback) {
        UserService userService = RestServiceGenerator.createService(UserService.class);
        Call<User> call = userService.getUserByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    callback.onSuccess(user);
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    private void updateUserDataInNav(User user) {
        View navHeaderView = binding.navView.getHeaderView(0);
        TextView nameTextView = navHeaderView.findViewById(R.id.txt_name_user);
        TextView emailTextView = navHeaderView.findViewById(R.id.txt_email_user);
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
    }
}
