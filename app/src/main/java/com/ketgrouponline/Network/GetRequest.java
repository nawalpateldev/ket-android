package com.ketgrouponline.Network;

import com.ketgrouponline.Model.HomeItemModel;
import com.ketgrouponline.Model.LoginModel;
import com.ketgrouponline.Model.RegisterModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GetRequest {

    @FormUrlEncoded
    @POST("/register")
    Call<RegisterModel> getRegisterData(@Field("name") String name, @Field("username") String username,
                                     @Field("email") String email, @Field("mobile") String mobile,
                                        @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Call<LoginModel> getLoginData(@Field("username") String username, @Field("password") String password);

    @GET("get-vendors")
    Call<HomeItemModel> getVendor(@Header("Authorization") String Authorization);

}
