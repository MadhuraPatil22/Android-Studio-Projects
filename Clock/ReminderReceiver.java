package com.example.clock;

import android.content.*;
import android.app.*;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String task = intent.getStringExtra("task");

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "reminder_channel";

        // For Android 8+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Reminders", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setContentTitle("Reminder")
                        .setContentText(task)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setAutoCancel(true);

        nm.notify(1, builder.build());
    }
}