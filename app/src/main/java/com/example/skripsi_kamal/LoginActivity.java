package com.example.skripsi_kamal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skripsi_kamal.apihelper.RestApi;
import com.example.skripsi_kamal.config.UserConfig;
import com.example.skripsi_kamal.config.UserObject;
import com.example.skripsi_kamal.config.Utilities;
import com.example.skripsi_kamal.model.BaseResponse;
import com.example.skripsi_kamal.model.LoginRequest;
import com.example.skripsi_kamal.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            if (etEmail.getText().length() == 0 || etPassword.getText().length() == 0){
                Utilities.toastError(findViewById(android.R.id.content),"Email atau password tidak boleh kosong");
            } else {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername(etEmail.getText().toString());
                loginRequest.setPassword(etPassword.getText().toString());

                Call<BaseResponse<LoginResponse>> postLogin = RestApi.get().api().loginRequest(loginRequest);
                postLogin.enqueue(new Callback<BaseResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<LoginResponse>> call, Response<BaseResponse<LoginResponse>> response) {
                        if (response.isSuccessful()){
                            if (response.body().getStatus() == 200){
                                LoginResponse loginResponse = response.body().getProfile();
                                UserObject userObject = new UserObject();
                                userObject.setUserEmail(loginRequest.getUsername());
                                userObject.setUserId(loginResponse.getId().toString());
                                UserConfig.getInstance().setCurrentUser(userObject);
                                UserConfig.getInstance().saveConfig(true);
                                Intent intent = new Intent(LoginActivity.this, FindActivity.class);
                                startActivity(intent);
                            }
                        }else {
                            Utilities.toastError(findViewById(android.R.id.content),"Password Tidak Cocok");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {

                    }
                });
            }
        });
    }
}