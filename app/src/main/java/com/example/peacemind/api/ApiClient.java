package com.example.peacemind.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Callback;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static String lastUsedBaseUrl = "";

    public static Retrofit getRetrofitInstance(Context context) {
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String baseUrl = sh.getString("url", "http://192.168.43.140:5000/");
        if (retrofit == null || !baseUrl.equals(lastUsedBaseUrl)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            lastUsedBaseUrl = baseUrl;
        }
        return retrofit;
    }

    public static void sendUserToServer(Context context, String name, String phone, String email) {
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(context);
        String baseUrl = sh.getString("url", "http://192.168.43.140:5000/");
        String SERVER_URL = baseUrl + "register_user";

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("phone", phone);
            json.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder().url(SERVER_URL).post(body).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("Server returned error: " + response.code());
                }
            }
        });
    }
}