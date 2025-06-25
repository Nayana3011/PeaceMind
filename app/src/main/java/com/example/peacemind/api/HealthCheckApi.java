package com.example.peacemind.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface HealthCheckApi {
    @GET("ping") // You can change this endpoint to any valid GET on your server
    Call<Void> ping();
}