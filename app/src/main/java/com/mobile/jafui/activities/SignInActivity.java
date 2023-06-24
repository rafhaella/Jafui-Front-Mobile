package com.mobile.jafui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class SignInActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button createAccountButton;
    private UserService userService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ImageButton buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> onBackPressed());

        // Inicializa as views
        nameEditText = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextPasswordConfirm);
        createAccountButton = findViewById(R.id.btn_sign_in);

        // Cria uma instância do UserService usando o RestServiceGenerator
        userService = RestServiceGenerator.createService(UserService.class);

        // Define o clique do botão "Criar conta"
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtém os valores dos inputs
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();


                // Validação de inputs
                // Verifica se os campos estão preenchidos
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifica se os campos de senha coincidem
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignInActivity.this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifica se o email é válido
                if (!isEmailValid(email)) {
                    Toast.makeText(SignInActivity.this, "Por favor, digite um email válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Cria um novo JSONObject que representa o novo usuário
                JSONObject newUserJson = new JSONObject();
                try {
                    newUserJson.put("name", name);
                    newUserJson.put("email", email);
                    newUserJson.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Cria um novo requestBody a partir do JSONObject
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), newUserJson.toString());

                // Chama o método de criação de usuário do UserService
                Call<User> call = userService.createUser(requestBody);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d("SignInActivity", "Resposta da chamada de criação de usuário: " + response.toString());

                        if (response.isSuccessful()) {
                            // Sucesso ao criar o usuário, redireciona para a tela de login
                            Toast.makeText(SignInActivity.this, "Conta criada com sucesso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Erro ao criar o usuário
                            String errorMessage;
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject errorJson = new JSONObject(response.errorBody().string());
                                    errorMessage = errorJson.optString("message");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    errorMessage = "Erro ao criar a conta";
                                }
                            } else {
                                errorMessage = "Erro ao criar a conta";
                            }
                            Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            Log.e("SignInActivity", "Erro ao criar a conta: " + errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        // Falha na chamada do método de criação de usuário
                        Toast.makeText(SignInActivity.this, "Falha ao criar a conta", Toast.LENGTH_SHORT).show();
                        Log.e("SignInActivity", "Falha na chamada de criação de usuário: " + t.getMessage());
                    }
                });

            }
        });
    }

    private boolean isEmailValid(String email) {
        // Expressão regular para validar o formato do email
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        // Verifica se o email é válido de acordo com a expressão regular
        return email.matches(regex);
    }
}

