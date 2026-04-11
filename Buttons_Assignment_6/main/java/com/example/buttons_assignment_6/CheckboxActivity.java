package com.example.buttons_assignment_6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;

public class CheckboxActivity extends AppCompatActivity {

    CheckBox checkBox9, checkBox10, checkBox11, checkBox12, checkBox13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkbox);

        checkBox9 = findViewById(R.id.checkBox9);
        checkBox10 = findViewById(R.id.checkBox10);
        checkBox11 = findViewById(R.id.checkBox11);
        checkBox12 = findViewById(R.id.checkBox12);
        checkBox13 = findViewById(R.id.checkBox13);

        checkBox9.setOnCheckedChangeListener((b, c) -> checkAll());
        checkBox10.setOnCheckedChangeListener((b, c) -> checkAll());
        checkBox11.setOnCheckedChangeListener((b, c) -> checkAll());
        checkBox12.setOnCheckedChangeListener((b, c) -> checkAll());

        checkBox13.setOnClickListener(v -> {
            Intent intent = new Intent(CheckboxActivity.this, ResultActivity.class);
            startActivity(intent);
        });
    }

    private void checkAll() {
        if (checkBox9.isChecked() &&
                checkBox10.isChecked() &&
                checkBox11.isChecked() &&
                checkBox12.isChecked()) {

            checkBox13.setEnabled(true);
        } else {
            checkBox13.setEnabled(false);
            checkBox13.setChecked(false);
        }
    }
}