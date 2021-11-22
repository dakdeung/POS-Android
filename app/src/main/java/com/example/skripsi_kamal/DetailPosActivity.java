package com.example.skripsi_kamal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.skripsi_kamal.R;
import com.example.skripsi_kamal.apihelper.RestApi;
import com.example.skripsi_kamal.apihelper.RetrofitClient;
import com.example.skripsi_kamal.config.UserConfig;
import com.example.skripsi_kamal.config.Utilities;
import com.example.skripsi_kamal.model.BaseArrayResponse;
import com.example.skripsi_kamal.model.BaseResponse;
import com.example.skripsi_kamal.model.CategoryResponse;
import com.example.skripsi_kamal.model.MenuResponse;
import com.example.skripsi_kamal.model.PaymentRequest;
import com.example.skripsi_kamal.model.PaymentResponse;
import com.example.skripsi_kamal.model.RemoveItemRequest;
import com.example.skripsi_kamal.model.TransactionResponse;
import com.example.skripsi_kamal.recyclerView.MenuAdapter;
import com.example.skripsi_kamal.recyclerView.MenuListAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPosActivity extends AppCompatActivity {

    TextView tvDate, tvTime, tvId,tvKasir, tvPayment, tvName, tvTotalCustomer, tvTotalAdmin, tvKembalian;
    EditText etBayar;
    Spinner spPayment;
    RecyclerView rvMenu;
    String transactionId, idTransaction, idPayment;
    Integer kembalian;
    ImageView btnBack,btnLogout;
    Button btnSimpan, btnTambah;
    MenuListAdapter menuListAdapter;
    RelativeLayout rLCustomer, rLAdmin;
    private List<MenuResponse> mResponseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pos);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvId = (TextView) findViewById(R.id.tv_id);
        tvName = (TextView) findViewById(R.id.tvNameCustomer);
        tvPayment = (TextView) findViewById(R.id.tv_payment);
        tvKasir = (TextView) findViewById(R.id.tv_kasir);
        tvTotalAdmin = (TextView) findViewById(R.id.tv_total);
        tvTotalCustomer = (TextView) findViewById(R.id.tv_customer_total);
        tvKembalian = (TextView) findViewById(R.id.tv_kembalian);
        etBayar = (EditText) findViewById(R.id.et_bayar);
        spPayment = (Spinner) findViewById(R.id.spinnerPayment);
        btnTambah = (Button) findViewById(R.id.btn_tambah) ;
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        btnLogout = (ImageView) findViewById(R.id.imv_logout);
        rvMenu = (RecyclerView) findViewById(R.id.rvMenu);
        rLCustomer = (RelativeLayout) findViewById(R.id.rl_customer);
        rLAdmin = (RelativeLayout) findViewById(R.id.rl_admin);
        btnBack = (ImageView) findViewById(R.id.btn_back);

        mResponseList = new ArrayList<>();

        Intent intent = getIntent();
        idTransaction = intent.getStringExtra("idTranscation");

        setLayout();
        setAdapter();
        getTranscantion();
        setSpinner();
        setTextOnChanged();
        onClick();
    }

    private void setLayout() {
        if (UserConfig.getInstance().getUserEmail().isEmpty()){
            tvPayment.setVisibility(View.VISIBLE);
            spPayment.setVisibility(View.GONE);
            rLCustomer.setVisibility(View.VISIBLE);
            rLAdmin.setVisibility(View.GONE);
            btnTambah.setVisibility(View.VISIBLE);
            btnSimpan.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        } else {
            tvPayment.setVisibility(View.GONE);
            spPayment.setVisibility(View.VISIBLE);
            rLCustomer.setVisibility(View.GONE);
            rLAdmin.setVisibility(View.VISIBLE);
            btnTambah.setVisibility(View.GONE);
            btnSimpan.setVisibility(View.VISIBLE);
        }
    }

    private void setTextOnChanged() {
        etBayar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etBayar.getText().length() > 0) {
                    kembalian = Integer.parseInt(etBayar.getText().toString()) - Integer.parseInt(tvTotalAdmin.getText().toString().substring(3).replace(",", ""));
                    tvKembalian.setText(String.valueOf(kembalian));
                } else {
                    tvKembalian.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onClick(){
        btnSimpan.setOnClickListener(v -> {
            if ( Integer.parseInt(idPayment) == 0){
                Utilities.toastError(findViewById(android.R.id.content),"Pilih Metode Bayar");
            } else if (etBayar.getText().length() == 0){
                Utilities.toastError(findViewById(android.R.id.content),"Uang Kurang");
            }else if (kembalian < 0){
                Utilities.toastError(findViewById(android.R.id.content),"Uang Kurang");
            }else{
                postPayment();
            }
        });

        btnTambah.setOnClickListener(v -> {
            openDialog();
        });
        btnBack.setOnClickListener(v -> onBackPressed());
        btnLogout.setOnClickListener(v -> {
            UserConfig.getInstance().clearConfig();
            Intent intent = new Intent(DetailPosActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openDialog() {
        Dialog dialog = new Dialog(idTransaction);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    private void postPayment(){
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentId(idPayment);
        paymentRequest.setTrxId(transactionId);
        paymentRequest.setUserId(UserConfig.getInstance().getUserId());
        Call<BaseResponse> postPayment = RestApi.get().api().putPayment(paymentRequest);
        postPayment.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Intent intent = new Intent(DetailPosActivity.this, PrintActivity.class);
                intent.putExtra("idTranscation", idTransaction);
                intent.putExtra("jumlahBayar", etBayar.getText().toString());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                Utilities.toastError(rootView, "Silahkan pilih tanggal terlebih dahulu");
            }
        });
    }

    private void setSpinner() {
        Call<BaseArrayResponse<PaymentResponse>> categoryList = RestApi.get().api().getPaymentList();
        categoryList.enqueue(new Callback<BaseArrayResponse<PaymentResponse>>() {
            @Override
            public void onResponse(Call<BaseArrayResponse<PaymentResponse>> call, Response<BaseArrayResponse<PaymentResponse>> response) {
                List<PaymentResponse> listCategory = response.body().getData();
                String[] itemsName = new String[response.body().getData().size()];
                String[] itemsId = new String[response.body().getData().size()];

                itemsName[0] = "Pilih";
                itemsId[0] = "0";
                //Traversing through the whole list to get all the names
                for(int i=1; i<response.body().getData().size(); i++){
                    //Storing names to string array
                    itemsName[i] = response.body().getData().get(i).getName();
                    itemsId[i] = response.body().getData().get(i).getId();
                }

                //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(DetailPosActivity.this, android.R.layout.simple_list_item_1, itemsName);
                //setting adapter to spinner
                spPayment.setAdapter(adapter);
                //Creating an array adapter for list view
                spPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        idPayment = String.valueOf(itemsId[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<BaseArrayResponse<PaymentResponse>> call, Throwable t) {

            }
        });
    }

    private void getTranscantion() {
        Call<BaseResponse<TransactionResponse>> get = RestApi.get().api().getTransaction(idTransaction);
        get.enqueue(new Callback<BaseResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<TransactionResponse>> call, Response<BaseResponse<TransactionResponse>> response) {
                TransactionResponse transactionResponse = response.body().getData();
                tvKasir.setText(UserConfig.getInstance().getUserEmail());
                tvId.setText(transactionResponse.getTransactionCode());
                tvName.setText(transactionResponse.getCustomer());
                tvDate.setText(transactionResponse.getCreatedAt().substring(0, 10));
                tvTime.setText(transactionResponse.getCreatedAt().substring(11));
                tvTotalAdmin.setText(transactionResponse.getSubTotal());
                tvTotalCustomer.setText(transactionResponse.getSubTotal());
                transactionId = transactionResponse.getId().toString();
            }

            @Override
            public void onFailure(Call<BaseResponse<TransactionResponse>> call, Throwable t) {
                Log.e("sssssssssssssssssss", t.getLocalizedMessage());
            }
        });
    }

    private void setAdapter() {
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.addItemDecoration(new DividerItemDecoration(this,0));
        rvMenu.setAdapter(menuListAdapter);
        menuListAdapter = new MenuListAdapter(this::ClickedMenu);

        Call<BaseResponse<TransactionResponse<MenuResponse>>> menuLists = RestApi.get().api().getMenuTransaction(idTransaction);
        menuLists.enqueue(new Callback<BaseResponse<TransactionResponse<MenuResponse>>>() {
            @Override
            public void onResponse(Call<BaseResponse<TransactionResponse<MenuResponse>>> call, Response<BaseResponse<TransactionResponse<MenuResponse>>> response) {
                mResponseList = response.body().getData().getMenu();
                menuListAdapter.setData(mResponseList);
                rvMenu.setAdapter(menuListAdapter);
            }

            @Override
            public void onFailure(Call<BaseResponse<TransactionResponse<MenuResponse>>> call, Throwable t) {

            }
        });
    }

    private void ClickedMenu(MenuResponse menuResponse) {
        HashMap<String, String> map = new HashMap<>();
        map.put("trxId", transactionId);
        map.put("id",  menuResponse.getId().toString());
        Call<BaseResponse> removeItem = RestApi.get().api().putRemoveItem(map);
        removeItem.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                setAdapter();
                getTranscantion();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

}