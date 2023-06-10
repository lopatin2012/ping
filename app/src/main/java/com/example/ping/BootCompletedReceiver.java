package com.example.ping;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            GlobalVariables globalVariables = (GlobalVariables) context.getApplicationContext();
            boolean startOnBoot = globalVariables.getPingFlag();
            if (startOnBoot) {
                Intent serviceIntent = new Intent(context, PingService.class);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}
