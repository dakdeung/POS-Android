package com.example.skripsi_kamal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skripsi_kamal.apihelper.RestApi;
import com.example.skripsi_kamal.config.UserConfig;
import com.example.skripsi_kamal.config.UserObject;
import com.example.skripsi_kamal.config.Utilities;
import com.example.skripsi_kamal.model.BaseResponse;
import com.example.skripsi_kamal.model.LoginResponse;
import com.example.skripsi_kamal.model.MenuResponse;
import com.example.skripsi_kamal.model.TransactionRequest;

import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText etNameCustomer;
    Button btnCreate, btnLogin, btnFind;
    String idTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNameCustomer = (EditText) findViewById(R.id.etNameCustomer);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnFind = (Button) findViewById(R.id.btnFind);


        btnFind.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserConfig.getInstance().isClientActivated()){
                    Intent intent = new Intent(MainActivity.this, FindActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindActivity.class);
                startActivity(intent);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNameCustomer.getText().length() == 0){
                    Utilities.toastError(findViewById(android.R.id.content), "Isi nama terlebih dahulu");
                }else{
                    TransactionRequest transactionRequest = new TransactionRequest();
                    transactionRequest.setCustomer(etNameCustomer.getText().toString());
                    Call<BaseResponse> createTransaction = RestApi.get().api().postTransaction(transactionRequest);
                    createTransaction.enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.isSuccessful()){
                                Intent intent = new Intent(MainActivity.this, PosActivity.class);
                                intent.putExtra("nameCustomer", etNameCustomer.getText().toString());
                                intent.putExtra("idTranscation", response.body().getTransactionCode());
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }
}