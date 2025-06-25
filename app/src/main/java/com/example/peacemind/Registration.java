package com.example.peacemind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.peacemind.api.ApiClient;
import com.example.peacemind.LoginApi;

import java.io.IOException;

import okhttp3.ResponseBody;

import com.example.peacemind.model.RegisterRequest;
import com.example.peacemind.model.RegisterResponse;
import com.example.peacemind.model.RegistrationRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity {
    EditText e1, e2, e3, e4, e5, e6;
    Button b1;
    SharedPreferences sh;

    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        e1 = findViewById(R.id.editTextTextPersonName8);   // First Name
        e2 = findViewById(R.id.editTextTextPersonName9);   // Last Name
        e3 = findViewById(R.id.editTextTextPersonName15);  // Phone
        e4 = findViewById(R.id.editTextTextPersonName16);  // Email
        e5 = findViewById(R.id.editTextTextPassword4);     // Password
        e6 = findViewById(R.id.editTextTextPassword5);     // Confirm Password
        b1 = findViewById(R.id.button14);                  // Register Button

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first_name = e1.getText().toString().trim();
                String last_name = e2.getText().toString().trim();
                String phone_no = e3.getText().toString().trim();
                String email = e4.getText().toString().trim();
                String password = e5.getText().toString().trim();
                String confirm_password = e6.getText().toString().trim();

                if (validateInput(first_name, last_name, phone_no, email, password, confirm_password)) {
                    registerUser(first_name, last_name, phone_no, email, password, confirm_password);
                }
            }
        });
    }

    private boolean validateInput(String first_name,String last_name, String phone, String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (first_name.isEmpty()) {
            e1.setError("Enter your first name");
            isValid = false;
        }

        if (last_name.isEmpty()) {
            e2.setError("Enter your last name");
            isValid = false;
        }

        if (phone.isEmpty()) {
            e3.setError("Enter phone number");
            isValid = false;
        } else if (phone.length() < 10) {
            e3.setError("Enter valid phone number");
            isValid = false;
        }

        if (email.isEmpty()) {
            e4.setError("Enter email address");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            e4.setError("Enter valid email address");
            isValid = false;
        }

        if (password.isEmpty()) {
            e5.setError("Enter password");
            isValid = false;
        } else if (password.length() < 6) {
            e5.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            e6.setError("Confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            e6.setError("Password mismatch");
            isValid = false;
        }

        return isValid;
    }

    private void registerUser(String first_name, String last_name, String phone, String email, String password, String confirm_password) {



        b1.setText("Registering...");
        b1.setEnabled(false);

        LoginApi api = ApiClient.getRetrofitInstance().create(LoginApi.class);
        RegistrationRequest request = new RegistrationRequest(first_name, last_name, phone, email, password, confirm_password);
        Call<RegisterResponse> call = api.registerUser(request);

//        call.enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//                b1.setEnabled(true);
//                b1.setText("Register");
//
//                if (response.isSuccessful()) {
//                    try {
//                        String body = response.body().string();
//                        if (body.contains("Registration Success")) {
//                            Toast.makeText(Registration.this, "✅ Registration Success", Toast.LENGTH_SHORT).show();
//                            Intent i = new Intent(getApplicationContext(), Login.class);
//                            startActivity(i);
//                            finish();
//                        } else if (body.contains("do not match")) {
//                            Toast.makeText(Registration.this, "❌ Passwords do not match", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(Registration.this, "⚠️ Unexpected response", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (IOException e) {
//                        Toast.makeText(Registration.this, "Error parsing response", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//                } else {
//                    Toast.makeText(Registration.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                b1.setEnabled(true);
//                b1.setText("Register");
//                Toast.makeText(Registration.this, "API Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });

//        Call<RegisterResponse> call1 = api.registerUser(request);
                call.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    b1.setText("REGISTER");
                    b1.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        RegisterResponse resp = response.body();
                        // Handle your custom RegisterResponse fields here
                        Toast.makeText(Registration.this, "✅ " + resp.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Registration.this, "⚠️ Registration failed", Toast.LENGTH_SHORT).show();
            }
        }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    // Reset button state
                    b1.setText("REGISTER");
                    b1.setEnabled(true);

                    // More specific error handling
                    String errorMessage;
                    if (t instanceof java.net.ConnectException) {
                        errorMessage = "Cannot connect to server. Please check your connection.";
                    } else if (t instanceof java.net.SocketTimeoutException) {
                        errorMessage = "Connection timeout. Please try again.";
                    } else {
                        errorMessage = "Network error: " + t.getMessage();
                    }

                    Toast.makeText(Registration.this, errorMessage, Toast.LENGTH_LONG).show();
                }
                });
    }

//    private void registerUser() {
//        // ... your existing code for getting form data ...
//
//        RegistrationRequest request = new RegisterRequest(firstName, lastName, phone, email, password, confirmPassword);
//
//        LoginApi apiService = RetrofitClient.getInstance().create(LoginApi.class);
//        Call<RegisterResponse> call = apiService.registerUser(request);
//
//        call.enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // Registration successful
//                    RegisterResponse regResponse = response.body();
//                    Toast.makeText(RegistrationActivity.this,
//                            "Registration successful: " + regResponse.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                    finish(); // Go back to login
//                } else {
//                    // Handle error response
//                    Toast.makeText(RegistrationActivity.this,
//                            "Registration failed: " + response.code(),
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
//                // This is where your timeout error is likely happening
//                Toast.makeText(RegistrationActivity.this,
//                        "Network error: " + t.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
