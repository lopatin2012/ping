package com.example.ping;


import static androidx.fragment.app.FragmentManager.TAG;

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
import android.util.Log;

import androidx.core.app.NotificationCompat;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class PingService extends Service {

    private static final int NOTIFICATION_ID = 200;
    public String errorMessage = "Красный экран";
    public volatile boolean isRunning = true;
    public boolean telegramFlag = false;
    public long connectionDropTime = System.currentTimeMillis();
    public long reconnectionTime = System.currentTimeMillis();
    private static final String botId = "5999996463:AAFEC5Gs66uypFtDTvuiolo4o7Yizp4UBLo";
    // Основной чат "-918846557"
    // Тестовый чат "-994059702"
    public final static String chatId = "-918846557";
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



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
        int time_ping = globalVariables.getTimePing() * 1000;
        startForeground(NOTIFICATION_ID, createNotification(true));
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        new Thread(() -> {
            receiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter("com.example.ping.notification");
            registerReceiver(receiver, filter);
            try {
                while (!Thread.interrupted() && isRunning) {
                    Thread.sleep(time_ping);
                    try {
                        // https://www.gosuslugi.ru
                        // https://app.okto.ru/users/sign_in
                        URL url = new URL("https://app.okto.ru/users/sign_in");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                        // Установка таймаутов соединения и чтение
                        urlConnection.setConnectTimeout(time_ping);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        int responseCode = urlConnection.getResponseCode();
//                        Log.e(TAG, String.valueOf(responseCode));

                        try (InputStream ignored = urlConnection.getInputStream()) {
                            // чтение данных из потока inputStream
                        } catch (IOException e) {
                            // обработка ошибки
                        }

                        urlConnection.disconnect();

                        switch (responseCode) {
                            case 200:
                                // Соединение успешно установлено
                                if (telegramFlag) {
                                    telegramFlag = false;
                                    reconnectionTime = System.currentTimeMillis();
                                    sendTelegramMessage(formatter);
                                    notificationManager.notify(NOTIFICATION_ID, createNotification(true));
                                }
                                break;
                            case 404:
                                // Обработка ошибки страница не найдена
                                handleNotFoundException();
                                break;
                            case 500:
                                // Обработка ошибки прокси-сервера
                                break;
                            default:
                                // Любой другой код ошибки
                                handleOtherErrors();
                                errorMessage = "Ошибка " + responseCode;
                                break;
                        }
                    } catch (SocketTimeoutException e) {
                        // Соединение не было установлено за отведённое время
                        handleConnectionTimeout();
                    } catch (IOException e) {
                        // Общая обработка ошибок ввода-вывода
                        handleIOException();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Завершение работы сервиса
            stopForeground(true);
            stopSelf(); // остановка сервиса, вызов onDestroy()
        }).start();
        return START_STICKY;
    }

    private void sendTelegramMessage(SimpleDateFormat formatter) {
        TelegramBot bot = new TelegramBot(botId);
        GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
        String terminalName = globalVariables.getNameTerminal();
        String message = LocalDate.now() + "\n" +
                "Терминал " + terminalName +  "\n" +
                errorMessage + "\n" +
                formatter.format(connectionDropTime) + " - OFF\n" +
                formatter.format(reconnectionTime) + " - ON\n" +
                ((reconnectionTime - connectionDropTime) / 1000) + " - LOST sec.";
        bot.execute(new SendMessage(chatId, message));
        globalVariables.setTextPing(message);
    }
    // Не удалось установить связь с сервером за 1 секунду
    private void handleConnectionTimeout() {
        if (!telegramFlag) {
            telegramFlag = true;
            errorMessage = "Ошибка TimeOut";
            connectionDropTime = System.currentTimeMillis();
            notificationManager.notify(NOTIFICATION_ID, createNotification(false));
        }
    }
    // Отсутствовал Интернет
    private void handleIOException() {
        if (!telegramFlag) {
            telegramFlag = true;
            errorMessage = "Отсуствовал Интернет";
            connectionDropTime = System.currentTimeMillis();
            notificationManager.notify(NOTIFICATION_ID, createNotification(false));
        }
    }
    // Страница 404
    private void handleNotFoundException() {
        if (!telegramFlag) {
            telegramFlag = true;
            errorMessage = "Страница не найдена";
            connectionDropTime = System.currentTimeMillis();
            notificationManager.notify(NOTIFICATION_ID, createNotification(false));
        }
    }
    // Необработанный ответ сервера
    private void handleOtherErrors() {
        if (!telegramFlag) {
            telegramFlag = true;
            errorMessage = "Необработанный ответ сервера";
            connectionDropTime = System.currentTimeMillis();
            notificationManager.notify(NOTIFICATION_ID, createNotification(false));
        }
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