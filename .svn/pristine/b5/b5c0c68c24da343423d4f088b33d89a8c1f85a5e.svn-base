package com.frxs.wmsrecpt.rest.service;


import com.frxs.wmsrecpt.model.Product;
import com.frxs.wmsrecpt.model.ReceivedOrder;
import com.frxs.wmsrecpt.model.ReceivedOrderDetalise;
import com.frxs.wmsrecpt.model.UserInfo;
import com.frxs.wmsrecpt.rest.model.ApiResponse;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService<T> {

    // 登录
    @FormUrlEncoded
    @POST("Packing/Login")
    Call<ApiResponse<UserInfo>> UserLogin(@FieldMap Map<String, Object> params);

    //修改密码
    @FormUrlEncoded
    @POST("Packing/ChangePassword")
    Call<ApiResponse<Boolean>> UserEditPassword(@FieldMap Map<String, Object> params);

    //获取商品信息
    @FormUrlEncoded
    @POST("Receive/GetReceiveProductInfo")
    Call<ApiResponse<List<Product>>> GetProductInfo(@FieldMap Map<String, Object> params);

    //获取收货单列表信息
    @FormUrlEncoded
    @POST("Receive/GetReceivedOrders")
    Call<ApiResponse<List<ReceivedOrder>>> getReceivedOrders(@FieldMap Map<String, Object> params);

    //获取收货单明细信息
    @FormUrlEncoded
    @POST("Receive/GetReceivedOrderDetails")
    Call<ApiResponse<ReceivedOrderDetalise>> getReceivedOrderDeatlis(@FieldMap Map<String, Object> params);

    // 完成收货
    @POST("Receive/CompletedReceivedOrder")
    Call<ApiResponse<Boolean>> CompletedReceivedOrder(@Body ReceivedOrder orderDetalise);
}
