package com.example.skripsi_kamal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.example.skripsi_kamal.apihelper.RestApi;
import com.example.skripsi_kamal.config.UserConfig;
import com.example.skripsi_kamal.config.Utilities;
import com.example.skripsi_kamal.model.BaseResponse;
import com.example.skripsi_kamal.model.MenuResponse;
import com.example.skripsi_kamal.model.TransactionResponse;
import com.example.skripsi_kamal.recyclerView.MenuListAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrintActivity extends AppCompatActivity {

    private final Locale locale = new Locale("id", "ID");
    private final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    TextView tvDate, tvTime, tvId,tvKasir, tvPayment, tvName, tvTotal, tvKembalian, tvBayar;
    RecyclerView rvMenu;
    String transactionId, idTransaction, jumlahBayar, date, time, payment, kasir, total, kembalian;
    ImageView btnBack,btnLogout;
    Button btnPrint, btnPrinter;
    MenuListAdapter menuListAdapter;
    private List<MenuResponse> mResponseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        initView();
        onClick();
        setAdapter();
        getTranscantion();
    }

    private void initView() {
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvId = (TextView) findViewById(R.id.tv_id);
        tvName = (TextView) findViewById(R.id.tvNameCustomer);
        tvPayment = (TextView) findViewById(R.id.tv_payment);
        tvKasir = (TextView) findViewById(R.id.tv_kasir);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvKembalian = (TextView) findViewById(R.id.tv_kembalian);
        tvBayar = (TextView) findViewById(R.id.tv_bayar);
        btnPrint = (Button) findViewById(R.id.btn_print) ;
        btnLogout = (ImageView) findViewById(R.id.imv_logout);
        rvMenu = (RecyclerView) findViewById(R.id.rvMenu);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnPrinter = (Button) findViewById(R.id.btn_printer);

        mResponseList = new ArrayList<>();

        Intent intent = getIntent();
        idTransaction = intent.getStringExtra("idTranscation");
        jumlahBayar = intent.getStringExtra("jumlahBayar");
    }

    private void onClick() {
        btnLogout.setOnClickListener(v -> {
            UserConfig.getInstance().clearConfig();
            Intent intent = new Intent(PrintActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        btnBack.setOnClickListener(v -> onBackPressed());
        btnPrint.setOnClickListener(view -> print());
        btnPrinter.setOnClickListener(view -> browseBluetoothDevice());
    }

    public static final int PERMISSION_BLUETOOTH = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PrintActivity.PERMISSION_BLUETOOTH:
                    this.print();
                    break;
            }
        }
    }

    private BluetoothConnection selectedDevice;

    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                items[++i] = device.getDevice().getName();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrintActivity.this);
            alertDialog.setTitle("Bluetooth printer selection");
            alertDialog.setItems(items, (DialogInterface.OnClickListener) (dialogInterface, i1) -> {
                int index = i1 - 1;
                if(index == -1) {
                    selectedDevice = null;
                } else {
                    selectedDevice = bluetoothDevicesList[index];
                }
                Button button = (Button) findViewById(R.id.btn_printer);
                button.setText(items[i1]);
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();

        }
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
                tvPayment.setText(transactionResponse.getPaymentName());
                tvTotal.setText(transactionResponse.getSubTotal());
                tvBayar.setText(jumlahBayar);

                int kembalian1 = Integer.parseInt(jumlahBayar) - Integer.parseInt(transactionResponse
                        .getSubTotal().substring(3).replace(",", ""));
                tvKembalian.setText(String.valueOf(kembalian1));

                kasir = UserConfig.getInstance().getUserEmail();
                date = transactionResponse.getCreatedAt().substring(0, 10);
                time = transactionResponse.getCreatedAt().substring(11);
                total = transactionResponse.getSubTotal();
                payment = transactionResponse.getPaymentName();
                kembalian = String.valueOf(kembalian1);
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
    }

    private void print() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, Integer.parseInt(PrintActivity.BLUETOOTH_SERVICE));
            } else {
                EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
                final String header = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n" +
                        "[L]\n" +
                        "[C]<u><font size='big'>ORDER "+idTransaction+"</font></u>\n" +
                        "[L]\n" +
                        "[C]================================\n" +
                        "[L]\n" +
                        "[L]<b>Payment: "+ payment +"</b>[R]"+ date +"\n" +
                        "[L]<b>Kasir  : "+ kasir +"</b>[R]"+ time +"\n" +
                        "[C]--------------------------------\n";
                String body = "";
                for (int i = 0; i < mResponseList.size(); i++){
                    body += "[C]"+ mResponseList.get(i).getName() +"[C]"+ mResponseList.get(i)
                            .getQty() +"[C]"+ mResponseList.get(i).getTotal() +"\n";
                }

                final String footer = "[C]--------------------------------\n" +
                        "[R]<b>SUBTOTAL  : "+ total +"</b>\n" +
                        "[R]<b>BAYAR     : "+ jumlahBayar +"</b>\n" +
                        "[R]<b>KEMBALIAN : "+ kembalian +"</b>\n";
                printer.printFormattedText(header + body + footer);

            }
        } catch (Exception e) {
            Log.e("APP", "Can't print", e);
        }
    }
}