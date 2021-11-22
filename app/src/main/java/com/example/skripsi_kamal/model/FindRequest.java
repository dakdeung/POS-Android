package com.example.skripsi_kamal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FindRequest {
    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;
}
