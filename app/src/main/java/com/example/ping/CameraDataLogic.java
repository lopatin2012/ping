package com.example.ping;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
        button_connect_camera = findViewById(R.id.button_connect_camera);
        web_view_camera = findViewById(R.id.web_view_camera);
        button_menu_main = findViewById(R.id.button_menu_main);

        button_connect_camera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onClick(View view) {
                GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
                String ip_camera = globalVariables.getAddressIp();
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