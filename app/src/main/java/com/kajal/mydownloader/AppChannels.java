package com.kajal.mydownloader;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppChannels extends Application {

    public static final String DOWNLOAD_CHANNEL_ID = "channel downloads";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channelDownloads = new NotificationChannel(
                    DOWNLOAD_CHANNEL_ID,
                    "Download progress",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channelDownloads.setDescription("This channel shows download progress while downloading");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelDownloads);

        }

    }

}
