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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

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

    public static File readAndWriteToFile(String sourcePath) {
        File sourceFile = new File(sourcePath);
        String fileName = sourceFile.getName();
        File targetFile = new File("/storage/emulated/0/350k_" + fileName); // файл на отправку в телеграмм

        try (BufferedReader reader = Files.newBufferedReader(sourceFile.toPath(), StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(targetFile.toPath(), StandardCharsets.UTF_8)) {

            int linesCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                linesCount++;
            }

            reader.close();

            LinkedList<String> lines = new LinkedList<>();
            int linesToRead = Math.min(linesCount, 350000);
            BufferedReader newReader = Files.newBufferedReader(sourceFile.toPath(), StandardCharsets.UTF_8);

            for (int i = 0; i < linesCount - linesToRead; i++) {
                newReader.readLine();
            }

            while ((line = newReader.readLine()) != null) {
                lines.add(line);
            }
            newReader.close();

            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
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

                bot.execute(new SendMessage("-994059702", "Подготовка файлов с терминала"));
                File file_duplicates = new File("/storage/emulated/0/duplicates.txt");
//                File file_queries = new File("/storage/emulated/0/queries.txt");
                File file_server_codes = new File("/storage/emulated/0/server_codes.txt");
                File file_videojet_codes = new File("/storage/emulated/0/videojet_codes.txt");
//                File file_videojet_requests = new File("/storage/emulated/0/videojet_requests.txt");

                // файл с дублями
                if (file_duplicates.exists()) {
                    bot.execute(new SendDocument("-994059702", readAndWriteToFile("/storage/emulated/0/duplicates.txt")));
                }
//                if (file_queries.exists()) {
//                    bot.execute(new SendDocument("-994059702", readAndWriteToFile("/storage/emulated/0/queries.txt"))); // функция не оптимизирована под этот файл
//                }
                // Коды с сервера
                if (file_server_codes.exists()) {
                    bot.execute(new SendDocument("-994059702", readAndWriteToFile("/storage/emulated/0/server_codes.txt")));
                }
                // Коды на принтер
                if (file_videojet_codes.exists()) {
                    bot.execute(new SendDocument("-994059702", readAndWriteToFile("/storage/emulated/0/videojet_codes.txt")));
                }
//                if (file_videojet_requests.exists()) {
//                    bot.execute(new SendDocument("-994059702", readAndWriteToFile("/storage/emulated/0/videojet_requests.txt")));
//                }
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