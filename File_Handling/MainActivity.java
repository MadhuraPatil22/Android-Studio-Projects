package com.example.filehandling;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    EditText name, roll, age, department, email, phone;
    Button submit, load;
    TextView result;

    String fileName = "students.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        roll = findViewById(R.id.roll);
        age = findViewById(R.id.age);
        department = findViewById(R.id.department);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        result = findViewById(R.id.result);

        submit = findViewById(R.id.submit);
        load = findViewById(R.id.load);

        // SAVE DATA
        submit.setOnClickListener(v -> {
            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);

                String data = name.getText().toString() + ","
                        + roll.getText().toString() + ","
                        + age.getText().toString() + ","
                        + department.getText().toString() + ","
                        + email.getText().toString() + ","
                        + phone.getText().toString() + "\n";

                fos.write(data.getBytes());
                fos.close();

                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

                // Clear fields
                name.setText(""); roll.setText(""); age.setText("");
                department.setText(""); email.setText(""); phone.setText("");

            } catch (Exception e) {
                Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show();
            }
        });

        // LOAD DATA IN TABLE FORMAT
        load.setOnClickListener(v -> {
            try {
                FileInputStream fis = openFileInput(fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

                StringBuilder table = new StringBuilder();

                // Table Header
                table.append("Sr.No   Name   Roll   Age   Dept   Email   Phone\n");
                table.append("-------------------------------------------------------------\n");

                String line;
                int count = 1;

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");

                    table.append(count).append("   ")
                            .append(parts[0]).append("   ")
                            .append(parts[1]).append("   ")
                            .append(parts[2]).append("   ")
                            .append(parts[3]).append("   ")
                            .append(parts[4]).append("   ")
                            .append(parts[5]).append("\n");

                    count++;
                }

                result.setText(table.toString());

                reader.close();
                fis.close();

            } catch (Exception e) {
                result.setText("No Data Found");
            }
        });
    }
}