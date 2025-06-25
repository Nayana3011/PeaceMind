package com.example.peacemind;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

public class APIClient {

    //public static final String BASE_URL = "http://192.168.42.101:5000/";
    private static final String SERVER_URL = "http://192.168.42.101:5000/register_user"; // Update with actual IP
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static void sendUserToServer(String name, String phone, String email) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("phone", phone);
            json.put("email", email);
        } catch (Exception e) {
            Log.e("APIClient", "JSON creation failed", e);
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("APIClient", "Failed to sync with server", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("APIClient", "Server sync successful");
                } else {
                    Log.e("APIClient", "Server sync failed with code: " + response.code());
                }
            }
        });
    }
}