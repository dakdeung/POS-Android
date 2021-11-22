package com.example.skripsi_kamal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentRequest {
    @SerializedName("trxId")
    @Expose
    private String trxId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("paymentId")
    @Expose
    private String paymentId;

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
