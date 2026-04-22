package com.example.menus_assignment_8;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Step 1: Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Step 2: Handle click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_add) {
            Toast.makeText(this, "Add clicked", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.menu_delete) {
            Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.menu_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.menu_exit) {
            finish();
        }

        return true;
    }
}