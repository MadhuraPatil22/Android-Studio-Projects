package com.example.buttons_assignment_6;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

public class ToggleActivity extends AppCompatActivity {

    ToggleButton toggleDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle);

        toggleDarkMode = findViewById(R.id.toggleDarkMode);

        toggleDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Dark Mode is ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Dark Mode is OFF", Toast.LENGTH_SHORT).show();
            }
        });
    }
}