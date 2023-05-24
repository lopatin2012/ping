package com.example.ping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    public Button button_save;
    public EditText terminal_name_edit, ip_camera_edit;
    public CheckBox flagPing_service;

    private GlobalVariables globalVariables; // объявляем переменную глобально

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings);
        terminal_name_edit = findViewById(R.id.terminal_name_edit);
        ip_camera_edit = findViewById(R.id.ip_camera_edit);
        flagPing_service = findViewById(R.id.flagPing_service);
        button_save = findViewById(R.id.button_save);

        // создаем экземпляр GlobalVariables с помощью контекста приложения
        globalVariables = (GlobalVariables) getApplicationContext();

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameTerminal = terminal_name_edit.getText().toString();
                String address_ip = ip_camera_edit.getText().toString();
                Boolean flag_ping = flagPing_service.isChecked();

                // используем уже созданный экземпляр globalVariables для сохранения настроек
                globalVariables.setNameTerminal(nameTerminal);
                globalVariables.setAddressIp(address_ip);
                globalVariables.setPingFlag(flag_ping);

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
