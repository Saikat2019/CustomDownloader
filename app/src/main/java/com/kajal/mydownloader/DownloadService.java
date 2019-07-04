package com.kajal.mydownloader;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.kajal.mydownloader.AppChannels.DOWNLOAD_CHANNEL_ID;

public class DownloadService extends Service {
    public static final String FILENAME = "fileName";
    public static final String URL = "downloadUrl";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "notification";

    public static final String TAG = "XXXDownload";

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationDownloading;
    private Notification notificationCompleted;

    private int downloadProgress = 0;
    private int maxProgress = 100;
    private int contentLength = -1;

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationDownloading = new NotificationCompat.Builder(this,DOWNLOAD_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Downloading...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, null))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(maxProgress,0,true)
                .setAutoCancel(true);

        notificationCompleted = new NotificationCompat.Builder(this,DOWNLOAD_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Download Successful")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, null))
                .setOngoing(false)
                .setAutoCancel(true)
                .build();

    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                downloadFile(intent.getStringExtra(URL),intent.getStringExtra(FILENAME));
            }
        };

        thread.start();

        return START_REDELIVER_INTENT;
    }


    protected void downloadFile(String URL, String FILENAME) {
        String urlPath = URL;
        String fileName = FILENAME;
        int result = Activity.RESULT_CANCELED;

//        Log.d(TAG, "downloadFile: 93");

        try {
            startForeground(10,notificationDownloading.build());
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            contentLength = connection.getContentLength();
            InputStream input = url.openStream();
//            Log.d(TAG, "downloadFile: length - "+contentLength);
//            Log.d(TAG, "downloadFile: 98");
            //The sdcard directory e.g. '/sdcard' can be used directly, or
            //more safely abstracted with getExternalStorageDirectory()
//            notificationManager.notify(10,notificationDownloading);

//            Log.d(TAG, "downloadFile: 105");

            File storagePath = new File(Environment.getExternalStorageDirectory() + "/Download");
            OutputStream output = new FileOutputStream(new File(storagePath, fileName));
//            Log.d(TAG, "downloadFile: 109");
            try {
//                Log.d(TAG, "downloadFile: 111");
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);

                    downloadProgress = downloadProgress + bytesRead;
                    notificationDownloading.setProgress(maxProgress,(int)((float)(downloadProgress * 100)/contentLength),false);
                    notificationManager.notify(10,notificationDownloading.build());

                }
                result = Activity.RESULT_OK;
            }
            catch (Exception e){
                e.printStackTrace();
            }finally{
                output.close();
                input.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        publishResults(result);
        stopSelf();
    }


    private void publishResults(int result) {
        notificationManager.notify(100,notificationCompleted);
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }




}
