package com.example.ping;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class GlobalVariables extends Application {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public GlobalVariables() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // получаем контекст приложения и создаем объект SharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences("global_variables", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setNameTerminal(String nameTerminal) {
        editor.putString("name_terminal", nameTerminal);
        editor.apply();
    }

    public String getNameTerminal() {
        return sharedPreferences.getString("name_terminal", "");
    }

    public void setAddressIp(String addressIp) {
        editor.putString("address_ip", addressIp);
        editor.apply();
    }

    public String getAddressIp() {
        return sharedPreferences.getString("address_ip", "");
    }

    public  void setPingFlag(Boolean pingFlag) {
        editor.putBoolean("ping_flag", pingFlag);
        editor.apply();
    }

    public boolean getPingFlag() {
        return sharedPreferences.getBoolean("ping_flag", false);
    }
}