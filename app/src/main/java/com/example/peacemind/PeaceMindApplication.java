package com.example.peacemind;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class PeaceMindApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

    }
}