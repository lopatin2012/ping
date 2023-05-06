package com.example.ping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public TextView resultTextView; // текст сверху
    public Button button_on, button_off, button_menu_camera; // кнопки старта и остановки пинга
    public boolean isRunning = false; // флаг цикла



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = findViewById(R.id.resultTextView);
        button_on = findViewById(R.id.button_on);
        button_off = findViewById(R.id.button_off);
        button_menu_camera = findViewById(R.id.button_menu_camera);


        button_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent check = new Intent(MainActivity.this, PingService.class);
                startForegroundService(check);
                String userInput = "Проверка запущена";
                resultTextView.setText(userInput);
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
    }
}