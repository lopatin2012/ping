package com.example.ping;


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

import androidx.core.app.NotificationCompat;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class PingService extends Service {

    private static final int NOTIFICATION_ID = 200;
    public boolean isTrueUrl200 = false;
    public volatile boolean isRunning = true;
    public boolean telegramFlag = false;
    public long connectionDropTime = System.currentTimeMillis();
    public long reconnectionTime = System.currentTimeMillis();
    private static final String botId = "5999996463:AAFEC5Gs66uypFtDTvuiolo4o7Yizp4UBLo";
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
//        Timer timer30minutes = new Timer();
        // Постановка задачи на отправку сообщения в телеграмм каждые 30 минут для теста
//        timer30minutes.scheduleAtFixedRate(new LogTask30(),0,30 * 60 * 1000);
        startForeground(NOTIFICATION_ID, createNotification(true));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        new Thread(() -> {
            receiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter("com.example.ping.notification");
            registerReceiver(receiver, filter);
            while (!Thread.interrupted() && isRunning) {
                try {
                    URL url = new URL("https://app.okto.ru");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try (InputStream inputStream = urlConnection.getInputStream()) {
                        // Пауза
                        Thread.sleep(200);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        urlConnection.setConnectTimeout(500);
                        int responseCode = urlConnection.getResponseCode();
                        urlConnection.disconnect();
                        isTrueUrl200 = responseCode == 200;
                        // Положительное уведомление
                        if (isTrueUrl200 && telegramFlag) {
                            telegramFlag = false;
                            reconnectionTime = System.currentTimeMillis();
                            TelegramBot bot = new TelegramBot(botId);
                            // Основной чат "-918846557"
                            // Тестовый чат "-994059702"
                            bot.execute(new SendMessage("-918846557", LocalDate.now() + "\n" +
                                    "Терминал Хамба\n" + formatter.format(connectionDropTime) + " - OFF\n" +
                                    formatter.format(reconnectionTime) + " - ON\n" +
                                    ((reconnectionTime - connectionDropTime) / 1000) + " - LOST sec."));
                            notificationManager.notify(NOTIFICATION_ID, createNotification(isTrueUrl200));
                        }
                        // Негативное событие. Любой код, отличный от 200, но не исключение
                        if (!isTrueUrl200 & !telegramFlag) {
                            telegramFlag = true;
                            connectionDropTime = System.currentTimeMillis();
                        }
                    }
                    // Обязательное закрытие соединения
                    //Негативное уведомление
                } catch (IOException | InterruptedException e) {
                    if (isRunning) {
                        notificationManager.notify(NOTIFICATION_ID, createNotification(false));
                        if (!telegramFlag) {
                            telegramFlag = true;
                            connectionDropTime = System.currentTimeMillis();
                        }
                    } else {
                        Thread.currentThread().interrupt();
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
            notificationManager.notify(NOTIFICATION_ID, notification);
            if (!isServerAvailable) {
                unregisterReceiver(receiver);
                isRunning = false;
                stopSelf();
            }
        }
    }
    // Функция на отправку сообщения в телеграмм каждые 30 минут для теста
//    private static class LogTask30 extends TimerTask{
//        @Override
//        public void run(){
//         TelegramBot bot = new TelegramBot(botId);
//            // Основной чат "-918846557"
//            // Тестовый чат "-994059702"
//            bot.execute(new SendMessage("-994059702", LocalDate.now() + "\n" +
//                    "Бот работает"));
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        isRunning = false;
        Thread.currentThread().interrupt();
    }
}