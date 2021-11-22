package com.example.skripsi_kamal;

import androidx.annotation.MenuRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.skripsi_kamal.apihelper.RestApi;
import com.example.skripsi_kamal.config.Utilities;
import com.example.skripsi_kamal.model.AddItemRequest;
import com.example.skripsi_kamal.model.BaseArrayResponse;
import com.example.skripsi_kamal.model.BaseResponse;
import com.example.skripsi_kamal.model.CategoryResponse;
import com.example.skripsi_kamal.model.MenuResponse;
import com.example.skripsi_kamal.model.MenuTransactionResponse;
import com.example.skripsi_kamal.model.TransactionResponse;
import com.example.skripsi_kamal.recyclerView.MenuAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PosActivity extends AppCompatActivity {

    TextView tvNamaCustomer;
    EditText etSearch;
    RecyclerView rvMenu;
    ImageView btnSearch;
    Button btnCart;
    MenuAdapter menuAdapter;
    Spinner spCategoty;
    String idTransaction, nameCustomer, transactionId;
    Boolean addItemsValue = false, isCategory = false;
    private List<MenuResponse> mResponseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        tvNamaCustomer = (TextView) findViewById(R.id.tvNameCustomer);
        rvMenu = (RecyclerView) findViewById(R.id.rvMenu);
        btnCart = (Button) findViewById(R.id.btn_cart);
        spCategoty = (Spinner) findViewById(R.id.spinnerCategory);
        btnSearch = (ImageView) findViewById(R.id.btn_search);
        etSearch = (EditText) findViewById(R.id.et_search);
        mResponseList = new ArrayList<>();

        Intent intent = getIntent();
        nameCustomer = intent.getStringExtra("nameCustomer");
        idTransaction = intent.getStringExtra("idTranscation");

        tvNamaCustomer.setText(nameCustomer);

        getData(idTransaction);
        setAdapter(0,false,"");
        setAdapterCategory();
        onClick();
    }

    private void setAdapterCategory() {
        Call<BaseArrayResponse<CategoryResponse>> categoryList = RestApi.get().api().getCategoryList();
        categoryList.enqueue(new Callback<BaseArrayResponse<CategoryResponse>>() {
            @Override
            public void onResponse(Call<BaseArrayResponse<CategoryResponse>> call, Response<BaseArrayResponse<CategoryResponse>> response) {
                List<CategoryResponse> listCategory = response.body().getData();
                String[] itemsName = new String[response.body().getData().size()];
                String[] itemsId = new String[response.body().getData().size()];

                itemsName[0] = "Pilih Category";
                itemsId[0]= "0";
                //Traversing through the whole list to get all the names
                for(int i=1; i<response.body().getData().size(); i++){
                    //Storing names to string array
                    itemsName[i] = response.body().getData().get(i).getName();
                    itemsId[i] = response.body().getData().get(i).getId();
                }

                //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(PosActivity.this, android.R.layout.simple_list_item_1, itemsName);
                //setting adapter to spinner
                spCategoty.setAdapter(adapter);
                //Creating an array adapter for list view


                spCategoty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        isCategory = true;
                        setAdapter(Integer.parseInt(itemsId[position]),false,"");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<BaseArrayResponse<CategoryResponse>> call, Throwable t) {

            }
        });
    }

    private void getData(String id) {
        Call<BaseResponse<TransactionResponse>> getTransaction = RestApi.get().api().getTransaction(id);
        getTransaction.enqueue(new Callback<BaseResponse<TransactionResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<TransactionResponse>> call, Response<BaseResponse<TransactionResponse>> response) {
                transactionId = response.body().getData().getId().toString();
            }

            @Override
            public void onFailure(Call<BaseResponse<TransactionResponse>> call, Throwable t) {

            }
        });
    }

    private void onClick() {
        btnCart.setOnClickListener(v -> {
            addItems();
        });
        btnSearch.setOnClickListener(view -> {
//            Log.e("sssssssssssssssssssss", etSearch.getText().toString());
            setAdapter(0,true,etSearch.getText().toString());
        });
    }

    private void addItems(){
        int count = 0;
        for (int i = 0;i < mResponseList.size();i++){
            if (mResponseList.get(i).getCount() > 0){
                count += 1;
            }
        }

        if (count > 0){
            for (int i = 0;i < mResponseList.size();i++){
                if (mResponseList.get(i).getCount() > 0){
                    count += 1;
                    AddItemRequest addItemRequest = new AddItemRequest();
                    addItemRequest.setTrxId(transactionId);
                    addItemRequest.setMenuId(mResponseList.get(i).getId().toString());
                    addItemRequest.setQty(mResponseList.get(i).getCount().toString());
                    Call<BaseResponse> addItem = RestApi.get().api().postAddItem(addItemRequest);
                    addItem.enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            Intent intent = new Intent(PosActivity.this, DetailPosActivity.class);
                            intent.putExtra("idTranscation", idTransaction);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            addItemsValue = false;
                        }
                    });
                }
            }
        } else {
            Utilities.toastError(findViewById(android.R.id.content),"Silakan Pilih Menu Terlebih Dahulu");
        }
    }

    private void setAdapter(int index, Boolean search, String value) {
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        rvMenu.addItemDecoration(new DividerItemDecoration(this,0));
        rvMenu.setAdapter(menuAdapter);
        menuAdapter = new MenuAdapter(this::ClickedMenu);

        if (search){
//            Log.e("ssssssssssssssssss", value);
            Call<BaseArrayResponse<MenuResponse>> menuSearchList = RestApi.get().api().getMenuSearchList(value);
            menuSearchList.enqueue(new Callback<BaseArrayResponse<MenuResponse>>() {
                @Override
                public void onResponse(Call<BaseArrayResponse<MenuResponse>> call, Response<BaseArrayResponse<MenuResponse>> response) {
                    mResponseList = response.body().getData();
                    menuAdapter.setData(mResponseList);
                    rvMenu.setAdapter(menuAdapter);
                    menuAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<BaseArrayResponse<MenuResponse>> call, Throwable t) {

                }
            });
        } else{
            if (index > 0 && isCategory) {
                Log.e("sssssssssssssss", "1");
                Call<BaseArrayResponse<MenuResponse>> menuFilterList = RestApi.get().api().getMenuFilterList(String.valueOf(index));
                menuFilterList.enqueue(new Callback<BaseArrayResponse<MenuResponse>>() {
                    @Override
                    public void onResponse(Call<BaseArrayResponse<MenuResponse>> call, Response<BaseArrayResponse<MenuResponse>> response) {
                        mResponseList = response.body().getData();
                        menuAdapter.setData(mResponseList);
                        rvMenu.setAdapter(menuAdapter);
                        menuAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<BaseArrayResponse<MenuResponse>> call, Throwable t) {

                    }
                });
            } else {
                Log.e("sssssssssssssss", "2");
                Call<BaseArrayResponse<MenuResponse>> menuList = RestApi.get().api().getMenuList();
                menuList.enqueue(new Callback<BaseArrayResponse<MenuResponse>>() {
                    @Override
                    public void onResponse(Call<BaseArrayResponse<MenuResponse>> call, Response<BaseArrayResponse<MenuResponse>> response) {
                        mResponseList = response.body().getData();
                        menuAdapter.setData(mResponseList);
                        rvMenu.setAdapter(menuAdapter);
                    }

                    @Override
                    public void onFailure(Call<BaseArrayResponse<MenuResponse>> call, Throwable t) {

                    }
                });
            }
        }
    }

    private void ClickedMenu(MenuResponse menuResponse) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
          PosActivity.this, R.style.BottomSheetDialogTheme
        );
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_menu,null);
        bottomSheetDialog.setContentView(view);
        TextView nama = (TextView)view.findViewById(R.id.tv_nama);
        TextView deskripsi = (TextView)view.findViewById(R.id.tv_deskripsi);
        TextView count = (TextView)view.findViewById(R.id.tv_count);
        ImageView imvProduct = (ImageView)view.findViewById(R.id.imv_produk);
        Button min = (Button)view.findViewById(R.id.btn_min);
        Button plus = (Button)view.findViewById(R.id.btn_plus);
        nama.setText(menuResponse.getName());
        deskripsi.setText(menuResponse.getDescription());
        try {
            Glide.with(this).load(menuResponse
                    .getImageUrl())
                    .into(imvProduct);
        } catch (Exception ex) {
            Timber.e(ex);
        }
        count.setText(String.valueOf(menuResponse.getCount()));

        if (menuResponse.getCount() > 0){
            min.setVisibility(View.VISIBLE);
        } else {
            min.setVisibility(View.GONE);
        }
        plus.setOnClickListener(view1 -> {
            int count1 = menuResponse.getCount();
            count1 = count1 + 1;
            menuResponse.setCount(count1);
            menuAdapter.notifyDataSetChanged();
            checkCount(menuResponse, min);
            count.setText(String.valueOf(count1));
        });
        min.setOnClickListener(view12 -> {
            int count1 = menuResponse.getCount();
            count1 = count1 - 1;
            menuResponse.setCount(count1);
            menuAdapter.notifyDataSetChanged();
            checkCount(menuResponse, min);
            count.setText(String.valueOf(count1));
        });
        bottomSheetDialog.show();
    }

    private void checkCount(MenuResponse menuResponse, Button min) {
        if (menuResponse.getCount() > 0){
            min.setVisibility(View.VISIBLE);
        } else{
            min.setVisibility(View.GONE);
        }
    }
}