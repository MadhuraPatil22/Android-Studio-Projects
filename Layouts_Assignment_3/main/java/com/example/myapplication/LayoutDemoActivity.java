package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LayoutDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_demo);

        Button btnLinear = findViewById(R.id.btnLinear);
        Button btnRelative = findViewById(R.id.btnRelative);
        Button btnConstraint = findViewById(R.id.btnConstraint);
        Button btnFrame = findViewById(R.id.btnFrame);
        Button btnTable = findViewById(R.id.btnTable);
        Button btnGrid = findViewById(R.id.btnGrid);
        Button btnAbsolute = findViewById(R.id.btnAbsolute);

        // ✅ EACH BUTTON → CORRECT ACTIVITY

        btnLinear.setOnClickListener(v ->
                startActivity(new Intent(LayoutDemoActivity.this, LinearActivity.class)));

        btnRelative.setOnClickListener(v ->
                startActivity(new Intent(LayoutDemoActivity.this, RelativeActivity.class)));

        btnConstraint.setOnClickListener(v ->
                startActivity(new Intent(LayoutDemoActivity.this, ConstraintActivity.class)));

        btnFrame.setOnClickListener(v ->
                startActivity(new Intent(LayoutDemoActivity.this, FrameActivity.class)));

        btnTable.setOnClickListener(v ->
                startActivity(new Intent(LayoutDemoActivity.this, TableActivity.class)));

        btnGrid.setOnClickListener(v ->
                startActivity(new Intent(LayoutDemoActivity.this, GridActivity.class)));

        btnAbsolute.setOnClickListener(v ->
                startActivity(new Intent(LayoutDemoActivity.this, AbsoluteActivity.class)));
    }
}