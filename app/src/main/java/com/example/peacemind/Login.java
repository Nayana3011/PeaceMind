package com.example.peacemind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseNetworkException;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.peacemind.model.LoginResponse;
import com.example.peacemind.api.ApiClient;
import com.example.peacemind.model.LoginRequest;

public class Login extends AppCompatActivity {
    EditText e1, e2;
    TextView t1, t2;
    Button b, serverBtn;
    ProgressBar progressBar;
    SharedPreferences sh;

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            FirebaseApp.initializeApp(this);
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Toast.makeText(this, "App initialization failed", Toast.LENGTH_LONG).show();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        e1 = findViewById(R.id.editTextTextEmailAddress);
        e2 = findViewById(R.id.editTextTextPassword);
        t1 = findViewById(R.id.textView58);
        //t2 = findViewById(R.id.textView139);
        b = findViewById(R.id.button);
        //serverBtn = findViewById(R.id.btnServerTrigger);
    }

    private void setupClickListeners() {
        t1.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Registration.class)));
       // t2.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), forgetpass.class)));

        b.setOnClickListener(v -> {
            String email = e1.getText().toString().trim();
            String password = e2.getText().toString().trim();

            if (validateInput(email, password)) {
                performServerLogin(email, password);
            }
        });

        //serverBtn.setOnClickListener(v -> checkServerStatus());
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;
        e1.setError(null);
        e2.setError(null);

        if (email.isEmpty()) {
            e1.setError("Enter email address");
            e1.requestFocus();
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            e1.setError("Enter valid email address");
            e1.requestFocus();
            isValid = false;
        }

        if (password.isEmpty()) {
            e2.setError("Enter password");
            if (isValid) e2.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            e2.setError("Password must be at least 6 characters");
            if (isValid) e2.requestFocus();
            isValid = false;
        }
        return isValid;
    }

    private void signInWithFirebase(String email, String password) {
        b.setEnabled(false);
        b.setText("Signing in...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    b.setEnabled(true);
                    b.setText("Login");

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user);
                            Toast.makeText(Login.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        }
                    } else {
                        String errorMessage = getDetailedErrorMessage(task.getException());
                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserData(FirebaseUser user) {
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor ed = sh.edit();
        ed.putString("user_id", user.getUid());
        ed.putString("user_email", user.getEmail());
        ed.putString("user_name", user.getDisplayName());
        ed.putBoolean("is_logged_in", true);
        ed.apply();
    }

    private void navigateToMainActivity() {
        Intent i = new Intent(getApplicationContext(), TFLiteEmotionClassifier.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private String getDetailedErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            return "No account found with this email address";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Incorrect email or password";
        } else if (exception instanceof FirebaseNetworkException) {
            return "Network error. Check your connection";
        } else {
            return "Login failed: " + (exception != null ? exception.getMessage() : "Unknown error");
        }
    }

//    private void checkServerStatus() {
//        String serverUrl = "http://192.168.42.101:5000/check_status";
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder().url(serverUrl).build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                e.printStackTrace(); // Optional: print to Logcat
//            }
//                //runOnUiThread(() -> Toast.makeText(Login.this, "❌ Server unreachable", Toast.LENGTH_SHORT).show());
//
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                runOnUiThread(() -> {
//                    if (response.isSuccessful()) {
//                        Toast.makeText(Login.this, "✅ Server OK", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(Login.this, "⚠️ Server error: " + response.code(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }

    private void checkServerStatus() {
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String baseUrl = sh.getString("url", "http://192.168.43.140:5000/"); // default fallback
        String serverUrl = baseUrl + "check_status";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(serverUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Login.this, "❌ API Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(Login.this, "✅ Server OK", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "⚠️ Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void performServerLogin(String email, String password) {
        //LoginResponse loginRequest = new LoginResponse(email, password);
        //LoginApi loginApi = ApiClient.getRetrofitInstance().create(LoginApi.class);
        LoginApi loginApi = ApiClient.getRetrofitInstance(getApplicationContext()).create(LoginApi.class);
        LoginRequest request = new LoginRequest(email, password);
        retrofit2.Call<LoginResponse> call = loginApi.loginUser(request);


        b.setEnabled(false);
        b.setText("Logging in...");

        call.enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                b.setEnabled(true);
                b.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        // Save or log user data
                        Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();

                        // You may want to store this in SharedPreferences
                        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        ed.putString("user_email", res.getUser().getEmail());
                        ed.putString("user_id", res.getUser().getId());
                        ed.putString("user_name", res.getUser().getName());
                        ed.putBoolean("is_logged_in", true);
                        ed.apply();
                        Intent intent = new Intent(Login.this, RealTimeDetectionActivity.class);
                        startActivity(intent);
                        finish();
                        //Intent i = new Intent(getApplicationContext(), TFLiteEmotionClassifier.class);
                        // navigateToMainActivity();
                    } else {
                        Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                b.setEnabled(true);
                b.setText("Login");
                Toast.makeText(Login.this, "API Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
