package com.example.streak;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply dark mode early to prevent flicker
        android.content.SharedPreferences prefs = getSharedPreferences("FitStreakPrefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("darkMode", false);
        int targetMode = isDark ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES : 
                                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
                                
        if (androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode() != targetMode) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(targetMode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Flash screen for 2.5 seconds then move to AuthActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                finish();
            }
        }, 2500);
    }
}
