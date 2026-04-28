package com.example.streak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        int id = intent.getIntExtra("id", 101);
        
        if (message != null && message.contains("water")) {
            NotificationHelper.showNotification(context, "Hydration Alert", message, id);
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String todayKey = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                String lastDone = snapshot.child("last_completion_date").getValue(String.class);
                
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                
                // 1. Check if today is completed
                if (!todayKey.equals(lastDone)) {
                    NotificationHelper.showNotification(context, "FitStreak Reminder", 
                            "Today's Exercises are yet to done! Don't forget your workout 🔥", 101);
                }

                // 2. Check if yesterday was missed (Specifically at 7am check)
                if (hour == 7) {
                    Calendar yest = Calendar.getInstance();
                    yest.add(Calendar.DAY_OF_YEAR, -1);
                    String yestMonthKey = new SimpleDateFormat("MM_yyyy_d", Locale.getDefault()).format(yest.getTime());
                    
                    if (!snapshot.child("history").hasChild(yestMonthKey)) {
                        NotificationHelper.showNotification(context, "Streak Broken", 
                                "You missed yesterday 😢. Start a new streak today!", 102);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
