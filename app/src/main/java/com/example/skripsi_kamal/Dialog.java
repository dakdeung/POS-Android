package com.example.skripsi_kamal;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {
    String idTransaction;

    public Dialog(String idTransaction) {
        this.idTransaction = idTransaction;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Information")
                .setMessage("Silakan Bayar Dikasir dengan kode bayar " + idTransaction)
                .setPositiveButton("ok", (dialogInterface, i) -> {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    ((Activity) getContext()).finish();
                });
        return builder.create();
    }
}
