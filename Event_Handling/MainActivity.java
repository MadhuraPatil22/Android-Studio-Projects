package com.example.longclickapp;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Button btnLongClick;
    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLongClick = findViewById(R.id.btnLongClick);
        textResult = findViewById(R.id.textResult);

        btnLongClick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                textResult.setText("Long Click Detected!");
                Toast.makeText(MainActivity.this,
                        "You pressed and held the button",
                        Toast.LENGTH_SHORT).show();

                return true; // important
            }
        });

        // (Optional) Normal Click
        btnLongClick.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this,
                    "Normal Click",
                    Toast.LENGTH_SHORT).show();
        });
    }
}