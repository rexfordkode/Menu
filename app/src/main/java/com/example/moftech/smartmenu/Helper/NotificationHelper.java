package com.example.moftech.smartmenu.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.moftech.smartmenu.R;

public class NotificationHelper extends ContextWrapper {
    private static final String SMENU_CHANEL_ID = "com.example.moftech.smartmenu.Menu";
    private static final String SMENU_CHANEL_NAME = "Smart Menu";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel smenuChannel = new NotificationChannel(SMENU_CHANEL_ID,
               SMENU_CHANEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        smenuChannel.enableLights(false);
        smenuChannel.enableVibration(true);
        smenuChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(smenuChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getSMenuChannelNotification(String title, String body, PendingIntent contentIntent,
                                                                        Uri soundUri)
    {
        return  new android.app.Notification.Builder(getApplicationContext(),SMENU_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false);
    }
    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getSMenuChannelNotification(String title, String body,
                                                                        Uri soundUri)
    {
        return  new android.app.Notification.Builder(getApplicationContext(),SMENU_CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false);
    }
}
