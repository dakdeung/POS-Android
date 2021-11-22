package com.example.skripsi_kamal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skripsi_kamal.apihelper.RestApi;
import com.example.skripsi_kamal.apihelper.RetrofitClient;
import com.example.skripsi_kamal.config.UserConfig;
import com.example.skripsi_kamal.config.Utilities;
import com.example.skripsi_kamal.model.BaseResponse;
import com.example.skripsi_kamal.model.MenuResponse;
import com.example.skripsi_kamal.model.MenuTransactionResponse;
import com.example.skripsi_kamal.model.TransactionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindActivity extends AppCompatActivity {
    
    Button btnFind, btnLogout;
    EditText etIdTranscation;
    String idTransaction;
    private List<TransactionResponse<MenuTransactionResponse>> mResponseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        btnFind = (Button) findViewById(R.id.btn_find);
        etIdTranscation = (EditText) findViewById(R.id.et_id_transaksi);
        btnLogout = (Button) findViewById(R.id.btn_logout);

        onClick();
    }

    private void onClick() {
        btnFind.setOnClickListener(v -> {
            if (etIdTranscation.getText().length() == 0) {
                Utilities.toastError(findViewById(android.R.id.content),"ID Transaksi tidak boleh kosong");
            } else {
                findTransaction();
            }
        });
        btnLogout.setOnClickListener(view -> {
            UserConfig.getInstance().clearConfig();
            Intent intent = new Intent(FindActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void findTransaction() {
        idTransaction = etIdTranscation.getText().toString();

        Call<BaseResponse<TransactionResponse>> get = RestApi.get().api().getTransaction(idTransaction);
        get.enqueue(new Callback<BaseResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<TransactionResponse>> call, Response<BaseResponse<TransactionResponse>> response) {
                if (response.isSuccessful()){
                    Intent intent = new Intent(FindActivity.this, DetailPosActivity.class);
                    intent.putExtra("nameCustomer", response.body().getData().getCustomer());
                    intent.putExtra("idTranscation", idTransaction);
                    startActivity(intent);
                } else {
                    Utilities.toastError(findViewById(android.R.id.content),"Data Tidak Ditemukan");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<TransactionResponse>> call, Throwable t) {

            }
        });
    }
}