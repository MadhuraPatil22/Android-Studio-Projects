//package com.example.checkbox;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.CheckBox;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class MainActivity extends AppCompatActivity {
//
//    CheckBox checkBox9,checkBox10,checkBox11,checkBox12,checkBox13;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//
//        checkBox9=findViewById(R.id.checkBox9);
//        checkBox10=findViewById(R.id.checkBox10);
//        checkBox11=findViewById(R.id.checkBox11);
//        checkBox12=findViewById(R.id.checkBox12);
//        checkBox13=findViewById(R.id.checkBox13);
//
//        checkBox9.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());
//        checkBox10.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());
//        checkBox11.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());
//        checkBox12.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());
//
//
//
////        checkBox9.setOnCheckedChangeListener(buttonView,isChecked)-> checkBox13());
////        checkBox10.setOnClickListener((buttonView,isChecked)-> checkBox13());
////        checkBox11.setOnClickListener((buttonView,isChecked)-> checkBox13());
////        checkBox12.setOnClickListener((buttonView,isChecked) -> checkBox13());
//
//
//        checkBox13.setOnClickListener(v-> {
//            Intent intent = new Intent(MainActivity.this ,ResultActivity.class);
//            startActivity(intent);
//        });
//
//        private void checkBox13 () {
//            if(checkBox9.isChecked() &&
//                    checkBox10.isChecked() &&
//                    checkBox11.isChecked() &&
//                    checkBox12.isChecked()){
//                        checkBox13.setEnabled(true);
//            }else{
//                checkBox13.setEnabled(false);
//                checkBox13.setChecked(false);
//            }
//        }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}

package com.example.checkbox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    CheckBox checkBox9, checkBox10, checkBox11, checkBox12, checkBox13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox9 = findViewById(R.id.checkBox9);
        checkBox10 = findViewById(R.id.checkBox10);
        checkBox11 = findViewById(R.id.checkBox11);
        checkBox12 = findViewById(R.id.checkBox12);
        checkBox13 = findViewById(R.id.checkBox13);

        // 🔹 Call method when any checkbox changes
        checkBox9.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());
        checkBox10.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());
        checkBox11.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());
        checkBox12.setOnCheckedChangeListener((buttonView, isChecked) -> checkAll());

        // 🔹 Click Done checkbox
        checkBox13.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
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