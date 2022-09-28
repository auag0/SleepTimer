package com.anago.sleeptimer;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(v -> {

            TimePicker timePicker = new TimePicker();
            timePicker.setClick(this::startTimer);
            timePicker.show(getSupportFragmentManager(), "timePicker");

        });

        Button stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(v -> {

            Intent intent = new Intent(this, NotificationService.class);
            stopService(intent);

        });
    }

    private void startTimer(int hour, int min) {
        int sec = 0;
        sec += hour * 3600;
        sec += min * 60;
        if (requestAdmin()) {
            Intent intent = new Intent(this, NotificationService.class);
            intent.setAction("start_notification");
            intent.putExtra("outtime", sec);
            startService(intent);
        }
    }

    private boolean requestAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, DeviceAdmin.class);
        if (devicePolicyManager.isAdminActive(componentName)) {
            return true;
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            startActivity(intent);
            return false;
        }
    }

    public static void LockScreen(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, DeviceAdmin.class);
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow();
        }
    }
}