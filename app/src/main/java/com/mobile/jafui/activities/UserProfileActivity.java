package com.mobile.jafui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.mobile.jafui.api.RestServiceGenerator;
import com.mobile.jafui.api.UserService;
import com.mobile.jafui.databinding.ActivityUserProfileBinding;
import com.mobile.jafui.entidades.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private EditText inputName, inputEmail, inputPassword, inputConfirmPassword, inputIdUser;
    private String userEmail;
    private String userId;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userEmail = getIntent().getStringExtra("email");

        TextInputLayout idUserInputLayout = binding.textInputIdUser;
        TextInputLayout fullNameInputLayout = binding.textInputName;
        TextInputLayout emailInputLayout = binding.textInputEmail;
        TextInputLayout passwordInputLayout = binding.textInputPassword;
        TextInputLayout confirmPasswordInputLayout = binding.textInputConfirmPassword;

        inputIdUser = idUserInputLayout.getEditText();
        inputName = fullNameInputLayout.getEditText();
        inputEmail = emailInputLayout.getEditText();
        inputPassword = passwordInputLayout.getEditText();
        inputConfirmPassword = confirmPasswordInputLayout.getEditText();

        // Inicialize o launcher
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                User updatedUser = (User) data.getSerializableExtra("user");
                                if (updatedUser != null) {
                                    // Atualize as views com os dados atualizados do usuário
                                    inputName.setText(updatedUser.getName());
                                    inputEmail.setText(updatedUser.getEmail());
                                    // ...
                                }
                            }
                        }
                    }
                });

        // Chama o método para obter os dados do usuário pelo email
        getUserData(userEmail);

        Button buttonSaveChanges = binding.buttonSaveChanges;
        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Atualize os dados do usuário no backend
                updateUser();
            }
        });

        ImageButton buttonBack = binding.buttonBack;
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button btnDeleteAccount = binding.buttonDeleteAccount;
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(view.getContext())
                .setTitle("Confirmar exclusão")
                .setMessage("Tem certeza que deseja excluir sua conta? Esta ação é irreversível.")
                .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUser(userId, new UserDeleteCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(UserProfileActivity.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(UserProfileActivity.this, "Erro ao excluir a conta", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
            }
        });

    }
    private void getUserData(String email) {
        // Crie uma instância do UserService usando o RestServiceGenerator
        UserService userService = RestServiceGenerator.createService(UserService.class);

        // Chame o método getUserByEmail
        Call<User> call = userService.getUserByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();

                    // Atualizar as views com as informações do usuário
                    inputIdUser.setText(user.getId());
                    inputName.setText(user.getName());
                    inputEmail.setText(user.getEmail());
                    inputPassword.setText(user.getPassword());
                    inputConfirmPassword.setText(user.getPassword());

                    userId = user.getId();

                    Log.d(TAG, "Dados do usuário obtidos com sucesso");
                    Log.d(TAG, "Id: " + user.getId());
                    Log.d(TAG, "Nome: " + user.getName());
                    Log.d(TAG, "Email: " + user.getEmail());
                    Log.d(TAG, "Senha: " + user.getPassword());
                } else {
                    Toast.makeText(UserProfileActivity.this, "Erro ao obter os dados do usuário", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Falha ao obter os dados do usuário");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Falha na chamada do serviço", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Falha na chamada do serviço", t);
            }
        });
    }

    private void updateUser() {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        // Verificar se os campos obrigatórios estão preenchidos
        if (!validateInputs(name, email, password, confirmPassword)) {
            return;
        }

        // Crie um objeto User com os dados atualizados
        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // Crie uma instância do UserService usando o RestServiceGenerator
        UserService userService = RestServiceGenerator.createService(UserService.class);

        // Chame o método updateUser no backend
        Call<User> call = userService.updateUser(userId, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User updatedUser = response.body();

                    // Exibir uma mensagem de sucesso
                    Toast.makeText(UserProfileActivity.this, "Dados do usuário atualizados com sucesso", Toast.LENGTH_SHORT).show();

                    // Criar um intent com os dados atualizados do usuário
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("user", updatedUser);

                    // Configurar o resultado como RESULT_OK e enviar o intent de retorno
                    setResult(RESULT_OK, resultIntent);

                    // Finalizar a atividade
                    finish();

                    // Atualizar as views com os novos dados do usuário
                    inputName.setText(updatedUser.getName());
                    inputEmail.setText(updatedUser.getEmail());
                    inputPassword.setText(updatedUser.getPassword());
                    inputConfirmPassword.setText(updatedUser.getPassword());

                    Log.d(TAG, "Dados do usuário atualizados com sucesso");
                    Log.d(TAG, "Id: " + updatedUser.getId());
                    Log.d(TAG, "Nome: " + updatedUser.getName());
                    Log.d(TAG, "Email: " + updatedUser.getEmail());
                    Log.d(TAG, "Senha: " + updatedUser.getPassword());

                } else {
                    Toast.makeText(UserProfileActivity.this, "Erro ao atualizar os dados do usuário", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Falha ao atualizar os dados do usuário");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Falha na chamada do serviço", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Falha na chamada do serviço", t);
            }
        });
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            inputName.setError("Digite um nome válido");
            inputName.requestFocus();
            return false;
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError("Digite um email válido");
            inputEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            inputPassword.setError("Digite uma senha válida");
            inputPassword.requestFocus();
            return false;
        }

        if (!confirmPassword.equals(password)) {
            inputConfirmPassword.setError("As senhas não correspondem");
            inputConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // Lógica para verificar se o formato do email é válido
        // Pode ser usado um padrão de expressão regular ou alguma outra validação
        // Neste exemplo, estou usando uma verificação básica para fins ilustrativos
        return email.contains("@");
    }

    private void deleteUser(String userId, final UserDeleteCallback callback) {
        UserService userService = RestServiceGenerator.createService(UserService.class);
        Call<Void> call = userService.deleteUser(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure();
            }
        });
    }

    public interface UserDeleteCallback {
        void onSuccess();
        void onFailure();
    }
}
