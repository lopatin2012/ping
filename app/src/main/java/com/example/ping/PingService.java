package com.example.ping;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PingService extends Service {

    private static final int NOTIFICATION_ID = 200;
    public boolean isTrueUrl200 = false;
    public volatile boolean isRunning = true;
    public boolean telegramFlag = false;
    public long connectionDropTime = System.currentTimeMillis();
    public long reconnectionTime = System.currentTimeMillis();
    private long lastNotificationTime = System.currentTimeMillis();
    private NotificationReceiver receiver;
    public NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(
                "ping_channel_id",
                "Ping Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification(true));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        new Thread(() -> {
            receiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter("com.example.ping.notification");
            registerReceiver(receiver, filter);
            while (!Thread.interrupted() && isRunning) {
                ImageView imageView = null;
                try {
                    URL url = new URL("https://app.okto.ru");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setConnectTimeout(1000);
                        urlConnection.connect();
                        isTrueUrl200 = urlConnection.getResponseCode() == 200;
                    } finally {
                        urlConnection.disconnect();
                    }
                    // Пауза 1 секунда
                    Thread.sleep(1000);
                    // Если прошло менее 5 секунд, то пропускаем уведомление
                    if (System.currentTimeMillis() - lastNotificationTime < 5000) {
                        continue;
                    }
                    // Положительное уведомление
                    if (isTrueUrl200 && telegramFlag) {
                        telegramFlag = false;
                        reconnectionTime = System.currentTimeMillis();
                        TelegramBot bot = new TelegramBot("5999996463:AAFEC5Gs66uypFtDTvuiolo4o7Yizp4UBLo");
                        bot.execute(new SendMessage("-918846557", LocalDate.now() + "\n" +
                               "Терминал Хамба\n" + formatter.format(connectionDropTime) + " - OFF\n" +
                                formatter.format(reconnectionTime) + " - ON\n" +
                                ((reconnectionTime - connectionDropTime) / 1000) + " - LOST sec."));
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.notify(NOTIFICATION_ID, createNotification(isTrueUrl200));
                    }
                    //Негативное уведомление
                } catch (IOException | InterruptedException e) {
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.notify(NOTIFICATION_ID, createNotification(false));

                    if (!telegramFlag) {
                        telegramFlag = true;
                        connectionDropTime = System.currentTimeMillis();
                    }
                }
            }
            stopForeground(true);
            stopSelf(); // остановка сервиса, вызов onDestroy()
        }).start();

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    private Notification createNotification(boolean isServerAvailable) {
        // Запускаем MainActivity при нажатии на уведомление
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        }

        // Создаем уведомление
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ping_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Проверка запущена")
                .setContentText(isServerAvailable ? "Сервер доступен" : "Сервер недоступен")
                .setTicker(isServerAvailable ? "Сервер доступен" : "Сервер недоступен")
                .setContentIntent(pendingIntent)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        lastNotificationTime = System.currentTimeMillis();
        return builder.build();
    }

    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isServerAvailable = intent.getBooleanExtra("isServerAvailable", false);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ping_channel_id")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Проверка запущена")
                    .setContentText(isServerAvailable ? "Сервер доступен" : "Сервер недоступен")
                    .setTicker(isServerAvailable ? "Сервер доступен" : "Сервер недоступен")
                    .setSound(null)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            if (!isRunning) {
                builder.setContentText("Сервер недоступен");
                builder.setTicker("Сервер недоступен");
            }

            Notification notification = builder.build();
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.notify(NOTIFICATION_ID, notification);
            if (!isServerAvailable) {
                unregisterReceiver(receiver);
                isRunning = false;
                stopSelf();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        isRunning = false;
        super.onDestroy();
    }
}