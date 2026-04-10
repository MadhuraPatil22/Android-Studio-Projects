package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GridActivity extends AppCompatActivity {

    TextView display;
    String current = "";
    String operator = "";
    double firstNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);

        display = findViewById(R.id.display);
    }

    public void onButtonClick(View view) {
        Button btn = (Button) view;
        String value = btn.getText().toString();

        if (value.matches("[0-9]")) {
            current += value;
            display.setText(current);
        }
        else if (value.matches("[+\\-*/]")) {
            operator = value;
            firstNumber = Double.parseDouble(current);
            current = "";
        }
        else if (value.equals("=")) {
            double secondNumber = Double.parseDouble(current);
            double result = 0;

            switch (operator) {
                case "+": result = firstNumber + secondNumber; break;
                case "-": result = firstNumber - secondNumber; break;
                case "*": result = firstNumber * secondNumber; break;
                case "/": result = secondNumber != 0 ? firstNumber / secondNumber : 0; break;
            }

            display.setText(String.valueOf(result));
            current = String.valueOf(result);
        }
        else if (value.equals("C")) {
            current = "";
            display.setText("0");
        }
    }
}