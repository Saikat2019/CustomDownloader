package com.kajal.mydownloader;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView downloadStatus;
    private EditText downloadUrl;
    private EditText fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadStatus = findViewById(R.id.download_status);
        downloadUrl = findViewById(R.id.download_url);
        fileName = findViewById(R.id.file_name);
        Button btnDownload = findViewById(R.id.btn_download);

        btnDownload.setOnClickListener(onDownloadListener());
    }

    private View.OnClickListener onDownloadListener() {
        return new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.putExtra(DownloadService.FILENAME, fileName.getText().toString());
                intent.putExtra(DownloadService.URL, downloadUrl.getText().toString());
                startService(intent);
                downloadStatus.setText("Downloading...");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, "File downloaded!", Toast.LENGTH_LONG).show();
                    downloadStatus.setText("Download completed!");
                } else {
                    Toast.makeText(MainActivity.this, "Error Downloading process!", Toast.LENGTH_LONG).show();
                    downloadStatus.setText("Download failed!");
                }
            }
        }
    };



}
