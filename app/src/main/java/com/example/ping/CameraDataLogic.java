package com.example.ping;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Scroller;

public class CameraDataLogic extends AppCompatActivity {

    // Объявляем переменные
    public Button button_connect_camera_master, button_connect_camera_slayer ,button_menu_main;
    public WebView web_view_camera;
    public ScrollView scroll_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_data_logic);
        // Находим значения
        button_connect_camera_master = findViewById(R.id.button_connect_camera_master);
        button_connect_camera_slayer = findViewById(R.id.button_connect_camera_slayer);
        web_view_camera = findViewById(R.id.web_view_camera);
        button_menu_main = findViewById(R.id.button_menu_main);
        scroll_camera = findViewById(R.id.scroll_camera);

        button_connect_camera_master.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View view) {
                GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
                String ip_camera = globalVariables.getAddressIpMaster();
                // автоматическая загрузка изображений
                web_view_camera.getSettings().setLoadsImagesAutomatically(true);
                // включение javascript
                web_view_camera.getSettings().setJavaScriptEnabled(true);
                web_view_camera.loadUrl("http://" + ip_camera + "/monitor");
                scroll_camera.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        button_connect_camera_slayer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View v) {
                GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
                String ip_camera = globalVariables.getAddressIpSlayer();
                // автоматическая загрузка изображений
                web_view_camera.getSettings().setLoadsImagesAutomatically(true);
                // включение javascript
                web_view_camera.getSettings().setJavaScriptEnabled(true);
                web_view_camera.loadUrl("http://" + ip_camera + "/monitor");
                scroll_camera.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        button_menu_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraDataLogic.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CameraDataLogic.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}