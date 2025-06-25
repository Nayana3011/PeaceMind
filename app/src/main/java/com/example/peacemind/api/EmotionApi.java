package com.example.peacemind.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import com.example.peacemind.model.EmotionResponse;

public interface EmotionApi {
    @Multipart
    @POST("/predict")
    Call<EmotionResponse> uploadFrame(@Part MultipartBody.Part frame);
}