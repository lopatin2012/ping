package com.example.ping;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public
    Button button_menu_camera, button_menu_file,
            button_menu_ping, button_menu_barcode_reading; // кнопки для перехода на другие менюшки


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_menu_ping = findViewById(R.id.button_menu_ping);
        button_menu_camera = findViewById(R.id.button_menu_camera);
        button_menu_barcode_reading = findViewById(R.id.button_menu_barcode_reading);
        button_menu_file = findViewById(R.id.button_menu_file);

        // Активити проверки доступа к серверу
        button_menu_ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PingManager.class));
                finish();// закрытие активити для освобожение ресурсов
            }
        });
        button_menu_barcode_reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BarcodeReadingManager.class));
                finish();
            }
        });
        // Активити просмотра камеры
        button_menu_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CameraDataLogic.class));
                finish(); // закрытие активити для освобожение ресурсов
            }
        });


        // Активити менеджера файлов
        button_menu_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FileManager.class));
                finish(); // закрытие активити для освобожение ресурсов
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem buttonSettings = menu.findItem(R.id.button_settings);
        buttonSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
        return true;
    }
}