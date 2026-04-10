package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);

        Button btnSky = findViewById(R.id.buttonSky);
        Button btnFlower = findViewById(R.id.buttonFlower);
        Button btnLake = findViewById(R.id.buttonLake);

        // SKY Background
        btnSky.setOnClickListener(v ->
                mainLayout.setBackgroundResource(R.drawable.sky));

        // FLOWER Background
        btnFlower.setOnClickListener(v ->
                mainLayout.setBackgroundResource(R.drawable.flower));

        // LAKE Background
        btnLake.setOnClickListener(v ->
                mainLayout.setBackgroundResource(R.drawable.lake));
    }
}
