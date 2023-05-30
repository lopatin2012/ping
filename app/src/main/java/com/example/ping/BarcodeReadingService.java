package com.example.ping;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

public class BarcodeReadingService extends Service {
    private final static String CHANNEL_ID = "Barcode_reading";
    private final static int NOTIFICATION_ID = 202;
    private Socket socket;
    private NotificationCompat.Builder builder;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Идёт сканирование")
                .setContentText("Коды записываются в файл")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String ipAddress = intent.getStringExtra("ipAddress");
        int port = intent.getIntExtra("port", 0);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Необходимо разрешение на чтение и запись в файловой системе");
        }
        File fileCode = new File(getExternalFilesDir(null), "codeCamera.txt");
        Log.e(TAG, "onStartCommand: Приложение запущено");

        startForeground(NOTIFICATION_ID, builder.build());
        isRunning = true;
        new Thread(() -> {
            try {
                Log.e(TAG, "Чтение в процессе");
                try {
                    socket = new Socket(ipAddress, port);
                }
                catch (ConnectException e) {
                    Log.e(TAG, "Не удалось установить соединение с камерой. Код ошибки\n" + e.getMessage());
                    stopForeground(true);
                    stopSelf();
                }
                if (socket == null) {
                    stopForeground(true);
                    stopSelf();
                }
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                FileOutputStream fileOutputStream = openFileOutput(fileCode.getName(), Context.MODE_PRIVATE | Context.MODE_APPEND);
                while (isRunning && (bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                stopForeground(true);
                stopSelf();
            }
        }).start();

        return START_NOT_STICKY;
    }

    public void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Barcode_reading",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}