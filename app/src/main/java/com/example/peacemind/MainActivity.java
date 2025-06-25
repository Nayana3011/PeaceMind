package com.example.peacemind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText ip;
    Button save;
    SharedPreferences sh;

    private static final String TAG = "MainActivity";

    @Override
    public void onBackPressed() {
        // Prevent going back from this screen
        // Comment this if you want default back behavior
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully in MainActivity");
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed in MainActivity", e);
        }

        setContentView(R.layout.activity_main);

        ip = findViewById(R.id.ip_input);
        save = findViewById(R.id.enter_button);

        // Shared preferences for storing IP and server URL
        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Load saved IP or set default
        String savedIp = sh.getString("ip", "192.168.42.101");
        ip.setText(savedIp);

        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String ipaddress = ip.getText().toString().trim();

        if (ipaddress.isEmpty()) {
            ip.setError("IP address is required");
            ip.requestFocus();
            return;
        }

        // Construct backend base URL
        String url = "http://" + ipaddress + ":5000/";

        // Save IP and URL in SharedPreferences
        SharedPreferences.Editor ed = sh.edit();
        ed.putString("ip", ipaddress);
        ed.putString("url", url);
        ed.apply();

        Log.d(TAG, "IP saved: " + ipaddress);
        Log.d(TAG, "URL saved: " + url);

        // Navigate to Login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}
