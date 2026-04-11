package com.example.rating_bar_assignment_7;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnRating, btnDownload, btnLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRating = findViewById(R.id.btnRating);
        btnDownload = findViewById(R.id.btnDownload);
        btnLoading = findViewById(R.id.btnLoading);

        btnRating.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RatingActivity.class)));

        btnDownload.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DownloadActivity.class)));

        btnLoading.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LoadingActivity.class)));
    }
}