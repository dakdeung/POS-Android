package com.example.skripsi_kamal.config;

public enum SnackActionType {
    OK(0),
    RETRY(1);
    final int type;

    SnackActionType(int type) {
        this.type = type;
    }
}
