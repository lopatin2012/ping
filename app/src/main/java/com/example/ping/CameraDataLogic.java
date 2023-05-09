package com.example.ping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class CameraDataLogic extends AppCompatActivity {

    // Объявляем переменные
    public EditText text_ip_camera;
    public Button button_connect_camera, button_menu_main;
    public WebView web_view_camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_data_logic);
        // Находим значения
        text_ip_camera = findViewById(R.id.text_ip_camera);
        button_connect_camera = findViewById(R.id.button_connect_camera);
        web_view_camera = findViewById(R.id.web_view_camera);
        button_menu_main = findViewById(R.id.button_menu_main);

        button_connect_camera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View view) {
                String ip_camera = text_ip_camera.getText().toString();
                // автоматическая загрузка изображений
                web_view_camera.getSettings().setLoadsImagesAutomatically(true);
                // включение javascript
                web_view_camera.getSettings().setJavaScriptEnabled(true);
                web_view_camera.loadUrl("http://" + ip_camera + "/monitor");
            }
        });
        button_menu_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraDataLogic.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}