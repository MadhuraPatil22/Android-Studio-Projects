//package com.example.togglebutton;
//
//import android.os.Bundle;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class MainActivity extends AppCompatActivity {
//
//    ToggleButton toggleDarkMode;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_main);
//
//        toggleDarkMode=findViewById(R.id.toggleDarkMode);
//
//        toggleDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if(isChecked){
//                Toast.makeText(this, "Dark Mode is ON", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                Toast.makeText(this, "Dark Mode is OFF", Toast.LENGTH_SHORT).show();
//            }
//        });
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//}
package com.example.togglebutton;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ToggleButton toggleDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toggleDarkMode = findViewById(R.id.toggleDarkMode);

        toggleDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Dark Mode is ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Dark Mode is OFF", Toast.LENGTH_SHORT).show();
            }
        });
    }
}