package com.example.buttons_assignment_6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnToggle, btnCheckBox, btnRadio, btnTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToggle = findViewById(R.id.btnToggle);
        btnCheckBox = findViewById(R.id.btnCheckBox);
        btnRadio = findViewById(R.id.btnRadio);

        btnToggle.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ToggleActivity.class)));

        btnCheckBox.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CheckboxActivity.class)));

        btnRadio.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RadioActivity.class)));

        }
}