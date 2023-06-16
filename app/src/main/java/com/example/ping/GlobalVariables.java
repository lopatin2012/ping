package com.example.ping;

import android.app.Application;
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

    // Используйте свой id для бота
    public String getBotId() {return sharedPreferences.getString("id_bot", "");}
    // Используйте свой id чата
    public String getBotChat() {return sharedPreferences.getString("bot_chat", "");}

    public void setNameTerminal(String nameTerminal) {
        editor.putString("name_terminal", nameTerminal);
        editor.apply();
    }

    public String getNameTerminal() {
        return sharedPreferences.getString("name_terminal", "");
    }

    public void setAddressIpMaster(String addressIp) {
        editor.putString("address_ip_master", addressIp);
        editor.apply();
    }

    public String getAddressIpMaster() {
        return sharedPreferences.getString("address_ip_master", "");
    }

    public void setAddressIpSlayer(String addressIp) {
        editor.putString("address_ip_slayer", addressIp);
        editor.apply();
    }

    public String getAddressIpSlayer() {
        return sharedPreferences.getString("address_ip_slayer", "");
    }

    public  void setPingFlag(Boolean pingFlag) {
        editor.putBoolean("ping_flag", pingFlag);
        editor.apply();
    }

    public boolean getPingFlag() {
        return sharedPreferences.getBoolean("ping_flag", false);
    }

    public void setTimePing(int timePing) {
        editor.putInt("time_ping", timePing);
        editor.apply();
    }

    public int getTimePing() {
        return sharedPreferences.getInt("time_ping", 30);
    }

    public void setTextPing(String textPing) {
        editor.putString("text_ping", textPing);
        editor.apply();
    }

    public String getTextPing() {
        return sharedPreferences.getString("text_ping", "Ошибок не было");
    }
}