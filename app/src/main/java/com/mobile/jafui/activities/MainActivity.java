package com.mobile.jafui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobile.jafui.R;
import com.mobile.jafui.api.RestServiceGenerator;
import com.mobile.jafui.api.UserService;
import com.mobile.jafui.entidades.User;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText ;
    private Button createAccountButton;

    private Button loginButton;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa as views
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        createAccountButton = findViewById(R.id.create_account_button);
        loginButton = findViewById(R.id.login_button);


        // Cria uma instância do UserService usando o RestServiceGenerator
        userService = RestServiceGenerator.createService(UserService.class);

        // Define o clique do botão "Entrar"
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtém os valores dos inputs
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Verifica se os inputs são válidos
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cria um novo JSONObject que representa o novo usuário
                JSONObject newLoginJson = new JSONObject();
                try {
                    newLoginJson.put("email", email);
                    newLoginJson.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Cria um novo requestBody a partir do JSONObject
                RequestBody requestBodyLogin = RequestBody.create(MediaType.parse("application/json"), newLoginJson.toString());

                // Chama o método de login do UserService
                Call<User> call = userService.userLogin(requestBodyLogin);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            // Login bem-sucedido, redireciona para a tela principal
                            Toast.makeText(MainActivity.this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();

                        } else {
                            // Login mal-sucedido
                            Toast.makeText(MainActivity.this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Falha na chamada do método de login
                        Toast.makeText(MainActivity.this, "Falha no login", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "Falha na chamada de login: " + t.getMessage());
                    }
                });
            }

        });

        // Define o clique do texto "Criar conta"
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redireciona para a tela de criação de conta
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }


}


