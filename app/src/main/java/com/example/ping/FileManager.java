package com.example.ping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;


public class FileManager extends AppCompatActivity {
    public Button button_send_files, button_menu_main_from_files;
    private static final int PERMISSION_STORAGE = 101;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceSaved) {
        super.onCreate(savedInstanceSaved);
        setContentView(R.layout.activity_file_manager);
        button_send_files = findViewById(R.id.button_send_files);
        button_menu_main_from_files = findViewById(R.id.button_menu_main_from_files);


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
    }
}
