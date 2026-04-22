package com.example.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.Intent;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    Button openCamera;
    ImageView imageView;
    RatingBar ratingBar;
    TextView ratingText;

    Bitmap capturedImage;

    // CAMERA RESULT
    ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            Intent data = result.getData();

                            if (data.getExtras() != null) {
                                capturedImage = (Bitmap) data.getExtras().get("data");

                                // Save dialog
                                new AlertDialog.Builder(this)
                                        .setTitle("Save Image?")
                                        .setMessage("Do you want to save this image?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            imageView.setImageBitmap(capturedImage);
                                        })
                                        .setNegativeButton("No", (dialog, which) -> {
                                            Toast.makeText(this, "Image discarded", Toast.LENGTH_SHORT).show();
                                        })
                                        .show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera = findViewById(R.id.openCamera);
        imageView = findViewById(R.id.imageView);
        ratingBar = findViewById(R.id.ratingBar);
        ratingText = findViewById(R.id.ratingText);

        // CAMERA PERMISSION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        // OPEN CAMERA
        openCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        });

        // RATING
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            ratingText.setText("Rating of the above image is " + rating);

            Toast.makeText(this,
                    "Rating of the image is " + rating,
                    Toast.LENGTH_SHORT).show();
        });
    }
}