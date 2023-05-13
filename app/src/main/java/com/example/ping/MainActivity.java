package com.example.ping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public TextView resultTextView; // текст сверху
    public Button button_on, button_off; // кнопки для контроля доступа к сайту
    public Button button_menu_camera, button_menu_file; // кнопки для перехода на другие менюшки
    public boolean isRunning = false; // флаг цикла для пинга
    public boolean onSaveInstanceState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = findViewById(R.id.resultTextView);
        button_on = findViewById(R.id.button_on);
        button_off = findViewById(R.id.button_off);
        button_menu_camera = findViewById(R.id.button_menu_camera);
        button_menu_file = findViewById(R.id.button_menu_file);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Запрашиваем разрешение, если оно не было ранее предоставлено.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }


        button_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent check = new Intent(MainActivity.this, PingService.class);
                startForegroundService(check);
                String userInput = "Проверка запущена";
                resultTextView.setText(userInput);
                isRunning = true;
            }
        });
        button_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, PingService.class));
                String userInput = "Проверка приостановлена";
                resultTextView.setText(userInput);
                isRunning = false;
            }
        });
        button_menu_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraDataLogic.class);
                startActivity(intent);
            }
        });
        button_menu_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FileManager.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isRunning) {
            onSaveInstanceState = true;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (onSaveInstanceState) {
            String userInput = "Проверка запущена";
            resultTextView.setText(userInput);
        }
    }
}