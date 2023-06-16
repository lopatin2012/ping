package com.example.ping;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.text.SimpleDateFormat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.datalogic.cradle.DLCradleManager;
import com.datalogic.decode.BarcodeID;
import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.DecodeResult;

public class BarcodeReadingService extends Service {
    private final static String CHANNEL_ID = "Barcode_reading";
    private final static int NOTIFICATION_ID = 202;
    private Socket socket;
    private BarcodeManager barcodeManager;
    private NotificationCompat.Builder builder;
    private boolean isRunning = false;
    private long saveCodeInFile = System.currentTimeMillis();

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

        File fileCode = new File("/storage/emulated/0/scanned_codes.txt");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
        Log.e(TAG, "onStartCommand: Приложение запущено");
        startForeground(NOTIFICATION_ID, builder.build());
        isRunning = true;
        new Thread(() -> {
            try {
                Log.e(TAG, "onStartCommand: Идёт сканирование");
                GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
                String ipAddress = globalVariables.getAddressIpMaster();
                socket = new Socket(ipAddress, 9999);
                Log.e(TAG, "Чтение в процессе");
                if (socket == null) {
                    stopForeground(true);
                    stopSelf();
                }
                Log.e(TAG, "onStartCommand: socket подключён");
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[2048];
                int bytesRead;
                FileWriter writer = new FileWriter(fileCode, true);
                while (isRunning && (bytesRead = inputStream.read(buffer)) != -1) {
                    saveCodeInFile = System.currentTimeMillis();
                    String codes = new String(buffer, 0, bytesRead);
                    String codes_final = codes.substring(5, 36);
                    Log.e(TAG, "onStartCommand: Где-то здесь коды " + codes_final);
                    writer.write(formatter.format(saveCodeInFile) + ";" + codes_final + "\n"); // запись в файл
                    writer.flush();
                }
                writer.close();
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