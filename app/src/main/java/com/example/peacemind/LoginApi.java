package com.example.peacemind;

import com.example.peacemind.model.LoginRequest;
import com.example.peacemind.model.LoginResponse;
import com.example.peacemind.model.RegisterRequest;
import com.example.peacemind.model.RegisterResponse;
import com.example.peacemind.model.RegistrationRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;
import okhttp3.ResponseBody;
public interface LoginApi {
    @Headers("Content-Type: application/json")
    @POST("login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);


    @Headers("Content-Type: application/json")
    @POST("register")
    Call<RegisterResponse> registerUser(@Body RegistrationRequest request);


}