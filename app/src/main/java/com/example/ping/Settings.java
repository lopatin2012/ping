package com.example.ping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    public Button button_save;
    public EditText terminal_name_edit, ip_camera_edit_master, ip_camera_edit_slayer, time_ping_edit;
    public CheckBox flagPing_service;

    private GlobalVariables globalVariables; // объявляем переменную глобально

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_settings);
        globalVariables = (GlobalVariables) getApplicationContext();
        terminal_name_edit = findViewById(R.id.terminal_name_edit);
        time_ping_edit = findViewById(R.id.time_ping_edit);
        ip_camera_edit_master = findViewById(R.id.ip_camera_edit_master);
        ip_camera_edit_slayer = findViewById(R.id.ip_camera_edit_slayer);
        flagPing_service = findViewById(R.id.flagPing_service);
        button_save = findViewById(R.id.button_save);

        // Установить значения из базы данных
        terminal_name_edit.setText(globalVariables.getNameTerminal());
        time_ping_edit.setText(String.valueOf(globalVariables.getTimePing()));
        ip_camera_edit_master.setText(globalVariables.getAddressIpMaster());
        ip_camera_edit_slayer.setText(globalVariables.getAddressIpSlayer());
        flagPing_service.setChecked(globalVariables.getPingFlag());




        // создаем экземпляр GlobalVariables с помощью контекста приложения


        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameTerminal = terminal_name_edit.getText().toString();
                int time_ping = Integer.parseInt(time_ping_edit.getText().toString());
                String address_ip_master = ip_camera_edit_master.getText().toString();
                String address_ip_slayer = ip_camera_edit_slayer.getText().toString();
                Boolean flag_ping = flagPing_service.isChecked();

                // используем уже созданный экземпляр globalVariables для сохранения настроек
                globalVariables.setNameTerminal(nameTerminal);
                globalVariables.setTimePing(time_ping);
                globalVariables.setAddressIpMaster(address_ip_master);
                globalVariables.setAddressIpSlayer(address_ip_slayer);
                globalVariables.setPingFlag(flag_ping);
                Toast.makeText(getApplicationContext(), "Настройки сохранены", Toast.LENGTH_LONG).show();
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
