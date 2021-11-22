package com.example.skripsi_kamal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuTransactionResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("total")
    @Expose
    private String total;
}
