package com.example.skripsi_kamal.apihelper;

import com.example.skripsi_kamal.model.AddItemRequest;
import com.example.skripsi_kamal.model.BaseArrayResponse;
import com.example.skripsi_kamal.model.BaseResponse;
import com.example.skripsi_kamal.model.CategoryResponse;
import com.example.skripsi_kamal.model.LoginRequest;
import com.example.skripsi_kamal.model.LoginResponse;
import com.example.skripsi_kamal.model.MenuResponse;
import com.example.skripsi_kamal.model.MenuTransactionResponse;
import com.example.skripsi_kamal.model.PaymentRequest;
import com.example.skripsi_kamal.model.PaymentResponse;
import com.example.skripsi_kamal.model.RemoveItemRequest;
import com.example.skripsi_kamal.model.TransactionRequest;
import com.example.skripsi_kamal.model.TransactionResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface BaseApiService {
    //User
    @POST("signin")
    Call<BaseResponse<LoginResponse>> loginRequest(@Body LoginRequest loginRequest);

    @GET("transaction/menu")
    Call<BaseArrayResponse<MenuResponse>> getMenuList();
    @GET("transaction/menu")
    Call<BaseArrayResponse<MenuResponse>> getMenuFilterList(@Query("categoryId") String params);
    @GET("transaction/menu")
    Call<BaseArrayResponse<MenuResponse>> getMenuSearchList(@Query("keyword") String params);
    @GET("transaction/category")
    Call<BaseArrayResponse<CategoryResponse>> getCategoryList();
    @GET("transaction/payment")
    Call<BaseArrayResponse<PaymentResponse>> getPaymentList();

    @GET("transaction/view")
    Call<BaseResponse<TransactionResponse>> getTransaction(@Query("transactionCode") String params);
    @GET("transaction/view")
    Call<BaseResponse<TransactionResponse<MenuResponse>>> getMenuTransaction(@Query("transactionCode") String params);

    @POST("transaction/create")
    Call<BaseResponse> postTransaction(@Body TransactionRequest transactionRequest);
    @POST("transaction/addItem")
    Call<BaseResponse> postAddItem(@Body AddItemRequest addItemRequest);

    @PUT("transaction/removeItem")
    Call<BaseResponse> putRemoveItem(@QueryMap Map<String, String> params);
    @PUT("transaction/process")
    Call<BaseResponse> putPayment(@Body PaymentRequest paymentRequest);
}
