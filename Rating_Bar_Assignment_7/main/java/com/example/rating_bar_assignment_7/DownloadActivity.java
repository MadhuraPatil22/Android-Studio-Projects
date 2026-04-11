package com.example.rating_bar_assignment_7;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class DownloadActivity extends AppCompatActivity {

    ProgressBar progressBar;
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        progressBar = findViewById(R.id.progressBar);
        Button btn = findViewById(R.id.btnStart);

        btn.setOnClickListener(v -> {
            progress = 0;
            Handler handler = new Handler();

            new Thread(() -> {
                while (progress <= 100) {
                    handler.post(() -> progressBar.setProgress(progress));
                    progress += 10;

                    try {
                        Thread.sleep(500);
                    } catch (Exception ignored) {}
                }
            }).start();
        });
    }
}