package com.example.ping;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

public class FileService extends Service {
    private final static String botId = "5999996463:AAFEC5Gs66uypFtDTvuiolo4o7Yizp4UBLo";
    private final static String botChatId_work = "-918846557";
    private final static String botChatId_test = "-994059702";
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

    public File readAndWriteToFile(File sourceFile, File targetFile) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(sourceFile, "r");
            long fileLength = file.length();
            long position = fileLength - 1; // начинаем с конца файла
            LinkedList<String> list = new LinkedList<>();
            while (position >= 0 && list.size() < 200000) { // читаем до начала файла или пока список не превысит лимит
                file.seek(position);
                char c = (char) file.readByte();

                if (c == '\n') { // если нашли символ переноса строки, добавляем строку в список
                    String line = file.readLine();
                    list.addFirst(line);
                }

                position--;
            }

            StringBuilder sb = new StringBuilder();
            for (String line : list) {
                sb.append(line).append("\n");
            }

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(targetFile));
                for (String line : list) {
                    writer.write(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return targetFile;
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

                File file_duplicates = new File("/storage/emulated/0/duplicates.txt");
                File file_queries = new File("/storage/emulated/0/queries.txt");
                File file_server_codes = new File("/storage/emulated/0/server_codes.txt");
                File file_videojet_codes = new File("/storage/emulated/0/videojet_codes.txt");
                File file_videojet_requests = new File("/storage/emulated/0/videojet_requests.txt");



                bot.execute(new SendMessage(botChatId_test, "Загрузка файлов с терминала\nЭто займёт некоторое время"));

                bot.execute(new SendDocument(botChatId_test, readAndWriteToFile(file_duplicates, file_duplicates)));
                bot.execute(new SendDocument(botChatId_test, readAndWriteToFile(file_queries, file_queries)));
                bot.execute(new SendDocument(botChatId_test, readAndWriteToFile(file_server_codes, file_server_codes)));
                bot.execute(new SendDocument(botChatId_test, readAndWriteToFile(file_videojet_codes, file_videojet_codes)));
                bot.execute(new SendDocument(botChatId_test, readAndWriteToFile(file_videojet_requests, file_videojet_requests)));
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