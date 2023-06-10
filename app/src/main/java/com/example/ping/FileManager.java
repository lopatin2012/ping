package com.example.ping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.io.File;


public class FileManager extends AppCompatActivity {
    public Button button_send_files, button_menu_main_from_files, button_delete_files;
    private static final int PERMISSION_STORAGE = 101;

    public void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] children = folder.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteFolder(child);
                }
            }
        }
        boolean deleted = folder.delete();
        if (!deleted) {
            System.out.println("Не удалось удалить папку " + folder.getAbsolutePath());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        setContentView(R.layout.activity_file_manager);
        button_send_files = findViewById(R.id.button_send_files);
        button_menu_main_from_files = findViewById(R.id.button_menu_main_from_files);
        button_delete_files = findViewById(R.id.button_delete_files);


        button_send_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.hasPermissions(FileManager.this)) {
                    Intent send_file = new Intent(FileManager.this, FileService.class);
                    startForegroundService(send_file);
                } else { PermissionUtils.requestPermissions(FileManager.this, PERMISSION_STORAGE);
                }
            }
        });
        button_menu_main_from_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileManager.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        button_delete_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  // Файлы внутри приложения ОКТО, которые необходимо чистить. Не работает без рут прав
//                @SuppressLint("SdCardPath") File barcode_cache = new File("/data/data/com.dubnovitsky.barcode/cache");
//                deleteDirectory(barcode_cache);
//                @SuppressLint("SdCardPath") File barcode_code_cache = new File("/data/data/com.dubnovitsky.barcode/code_cache");
//                deleteDirectory(barcode_code_cache);
//                @SuppressLint("SdCardPath") File barcode_databases = new File("/data/data/com.dubnovitsky.barcode/databases");
//                deleteDirectory(barcode_databases);
//                @SuppressLint("SdCardPath") File barcode_files = new File("/data/data/com.dubnovitsky.barcode/files");
//                deleteDirectory(barcode_files);

                // файлы ОКТО
                File file_duplicates = new File("/storage/emulated/0/duplicates.txt");
                File file_queries = new File("/storage/emulated/0/queries.txt");
                File file_server_codes = new File("/storage/emulated/0/server_codes.txt");
                File file_videojet_codes = new File("/storage/emulated/0/videojet_codes.txt");
                File file_videojet_requests = new File("/storage/emulated/0/videojet_requests.txt");

                // файлы утилиты
                File file_util_duplicates = new File("/storage/emulated/0/350k_duplicates.txt");
                File file_util_server_codes = new File("/storage/emulated/0/350k_server_codes.txt");
                File file_util_videojet_codes = new File("/storage/emulated/0/350k_videojet_codes.txt");

                //проверка и удаление файлов утилиты
                if (file_util_duplicates.exists()) {
                    file_util_duplicates.delete();
                }
                if (file_util_server_codes.exists()) {
                    file_util_server_codes.delete();
                }
                if (file_util_videojet_codes.exists()) {
                    file_util_videojet_codes.delete();
                }

                // проверка и удаление файлов ОКТО
                if (file_duplicates.exists()) {
                    file_duplicates.delete();
                }
                if (file_queries.exists()) {
                    file_queries.delete();
                }
                if (file_server_codes.exists()) {
                    file_server_codes.delete();
                }
                if (file_videojet_codes.exists()) {
                    file_videojet_codes.delete();
                }
                if (file_videojet_requests.exists()) {
                    file_videojet_requests.delete();
                }
                  // чистка кэша приложения ОКТО. Не работает без рут прав
//                /data/data/com.dubnovitsky.barcode/cache
//                 /data/data/com.dubnovitsky.barcode/code_cache
//                /data/data/com.dubnovitsky.barcode/databases
//                /data/data/com.dubnovitsky.barcode/files


                Toast.makeText(getApplicationContext(), "Файлы удалены", Toast.LENGTH_LONG).show();
            }
//            public static void deleteDirectory(File directory) {
//                if(directory.exists()){
//                    File[] files = directory.listFiles();
//                    if(null!=files){
//                        for (File file : files) {
//                            if (file.isDirectory()) {
//                                deleteDirectory(file);
//                            } else {
//                                file.delete();
//                            }
//                        }
//                    }
//                    directory.delete();
//                }
//            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FileManager.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
