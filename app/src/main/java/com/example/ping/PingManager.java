package com.example.ping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PingManager extends AppCompatActivity {

    public  Button button_menu_main_from_ping; // Кнопка для возврата в главное меню
    public TextView resultTextView; // Текст для отображения статуса проверки сервера
    public Button button_ping_on, button_ping_off; // Кнопки для запуска и остановки проверки сервера


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
        button_menu_main_from_ping = findViewById(R.id.button_menu_main_from_ping);
        resultTextView = findViewById(R.id.resultTextView);
        button_ping_on = findViewById(R.id.button_ping_on);
        button_ping_off = findViewById(R.id.button_ping_off);
        resultTextView.setText(globalVariables.getTextPing());



        button_menu_main_from_ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PingManager.this, MainActivity.class);
                startActivity(intent);
                finish(); // Закрытие текущей активити
            }
        });
        button_ping_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startForegroundService(new Intent(PingManager.this, PingService.class));
            }
        });
        button_ping_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(PingManager.this, PingService.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PingManager.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
