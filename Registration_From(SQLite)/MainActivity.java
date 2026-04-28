package com.example.registrationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    EditText name, age, dob, email, phone;
    RadioGroup genderGroup;
    Button submit;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        dob = findViewById(R.id.dob);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        genderGroup = findViewById(R.id.genderGroup);
        submit = findViewById(R.id.submit);

        db = new DBHelper(this);

        submit.setOnClickListener(v -> {

            // ✅ VALIDATION
            if(name.getText().toString().isEmpty() ||
                    age.getText().toString().isEmpty() ||
                    dob.getText().toString().isEmpty() ||
                    email.getText().toString().isEmpty() ||
                    phone.getText().toString().isEmpty()) {

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = genderGroup.getCheckedRadioButtonId();

            if(selectedId == -1){
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGender = findViewById(selectedId);
            String gender = selectedGender.getText().toString();

            boolean inserted = db.insertData(
                    name.getText().toString(),
                    age.getText().toString(),
                    dob.getText().toString(),
                    email.getText().toString(),
                    phone.getText().toString(),
                    gender
            );

            if (inserted) {
                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

                // 🔄 CLEAR ALL FIELDS
                name.setText("");
                age.setText("");
                dob.setText("");
                email.setText("");
                phone.setText("");
                genderGroup.clearCheck();

                // 🎯 Focus back to first field
                name.requestFocus();

            } else {
                Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}