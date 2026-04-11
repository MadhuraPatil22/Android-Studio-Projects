package com.example.rating_bar_assignment_7;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button btn = findViewById(R.id.btnSubmit);

        btn.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            Toast.makeText(this, "Rating: " + rating, Toast.LENGTH_SHORT).show();
        });
    }
}