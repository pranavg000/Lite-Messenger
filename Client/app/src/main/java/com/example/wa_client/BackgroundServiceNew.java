package com.example.wa_client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;

public class BackgroundServiceNew extends JobIntentService {
    public BackgroundServiceNew() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = getString(R.string.app_name);
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_layout);

        Notification notification =
                new Notification.Builder(this,channelId).setCustomContentView(remoteViews).build();
        startForeground(1, notification);
        Intent intent1 = new Intent(this, BackgroundService.class);
        startService(intent1);
    }
}