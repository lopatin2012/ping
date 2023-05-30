package com.example.ping;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class BarcodeReadingManager extends AppCompatActivity {

    public Button button_main_menu, button_camera_read_on, button_camera_read_off; // кнопки в активити
    private static final int PERMISSION_STORAGE = 101;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reading);
        button_main_menu = findViewById(R.id.button_main_menu);
        button_camera_read_on = findViewById(R.id.button_camera_read_on);
        button_camera_read_off = findViewById(R.id.button_camera_read_off);

        button_main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BarcodeReadingManager.this, MainActivity.class));
                finish();
            }
        });
        button_camera_read_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.hasPermissions(BarcodeReadingManager.this)) {
                    Intent intent = new Intent(BarcodeReadingManager.this, BarcodeReadingService.class);
                    startForegroundService(intent);
                    Log.e(TAG, "Чтение запущено");
                }
                else {
                    PermissionUtils.requestPermissions(BarcodeReadingManager.this, PERMISSION_STORAGE);
                }

            }
        });
        button_camera_read_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "Чтение остановлено");
                stopService(new Intent(BarcodeReadingManager.this, BarcodeReadingService.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BarcodeReadingManager.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
