package com.example.skripsi_kamal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponse<T> {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("transactionCode")
    @Expose
    private String transactionCode;
    @SerializedName("profile")
    @Expose
    private T profile = null;
    @SerializedName("data")
    @Expose
    private T data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getProfile() {
        return profile;
    }

    public void setProfile(T profile) {
        this.profile = profile;
    }
}
