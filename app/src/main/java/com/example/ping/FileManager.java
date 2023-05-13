package com.example.ping;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;

import java.io.File;

public class FileManager extends AppCompatActivity {
    public Button button_send_files;
    public int flagReadFiles = 0;

    @Override
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        setContentView(R.layout.activity_file_manager);
        button_send_files = findViewById(R.id.button_send_files);


        button_send_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send_file = new Intent(FileManager.this, FileService.class);
                startForegroundService(send_file);
            }
        });
    }
}
