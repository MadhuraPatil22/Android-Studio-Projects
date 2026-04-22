package com.example.clock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText task;
    Button selectDate, selectTime, setReminder;
    TextView currentTime, reminderTime;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        task = findViewById(R.id.task);
        selectDate = findViewById(R.id.selectDate);
        selectTime = findViewById(R.id.selectTime);
        setReminder = findViewById(R.id.setReminder);
        currentTime = findViewById(R.id.currentTime);
        reminderTime = findViewById(R.id.reminderTime);

        // ✅ Ask Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Show current time
        currentTime.setText("Current Time: " + sdf.format(calendar.getTime()));

        // DATE PICKER
        selectDate.setOnClickListener(v -> {
            DatePickerDialog dp = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            dp.getDatePicker().setMinDate(System.currentTimeMillis());
            dp.show();
        });

        // TIME PICKER
        selectTime.setOnClickListener(v -> {
            TimePickerDialog tp = new TimePickerDialog(this,
                    (view, hour, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);

                        // 🔥 Show selected time BIG
                        reminderTime.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false);

            tp.show();
        });

        // SET REMINDER
        setReminder.setOnClickListener(v -> {

            if (task.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter task!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(this, "Select future time!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("task", task.getText().toString());

            PendingIntent pi = PendingIntent.getBroadcast(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

            Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show();
        });
    }
}