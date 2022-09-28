package com.anago.sleeptimer;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    int notificationId = 11827;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;

    private Timer timer;
    private int outtime = 60;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onCreate() {
        notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("SleepTimer", getString(R.string.timer), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent cancel = new Intent();
        cancel.setAction("Cancel");
        PendingIntent cancelPendingIntent =
                PendingIntent.getBroadcast(this, 0, cancel, 0);

        Intent plus5 = new Intent();
        plus5.setAction("Plus5");
        PendingIntent plus5PendingIntent =
                PendingIntent.getBroadcast(this, 0, plus5, 0);

        Intent plus10 = new Intent();
        plus10.setAction("Plus10");
        PendingIntent plus10PendingIntent =
                PendingIntent.getBroadcast(this, 0, plus10, 0);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Cancel");
        intentFilter.addAction("Plus5");
        intentFilter.addAction("Plus10");

        registerReceiver(new NotificationService.BroadcastReceiver(), intentFilter);

        builder = new NotificationCompat.Builder(this, "SleepTimer");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Sleep Timer");
        builder.setContentText("null");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setOngoing(true);
        builder.addAction(R.drawable.ic_cancel, getString(R.string.cancel), cancelPendingIntent);
        builder.addAction(R.drawable.ic_plus_one, "+5", plus5PendingIntent);
        builder.addAction(R.drawable.ic_plus_one, "+10", plus10PendingIntent);

        NotificationService.timerTask timerTask = new timerTask();
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            if(intent.getAction().equals("start_notification")) {
                if (notificationManager != null && builder != null) {
                    outtime = intent.getIntExtra("outtime", 0);
                    updateNotification();
                }
            }
        }

        return START_STICKY;
    }

    class timerTask extends TimerTask{
        @Override
        public void run() {
            if(outtime > 0) {
                outtime--;
                updateNotification();
            }else{
                MainActivity.LockScreen(NotificationService.this);
                finish();
            }
        }
    }

    class BroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && intent.getAction() != null){
                switch (intent.getAction()){
                    case "Cancel":
                        finish();
                        break;
                    case "Plus5":
                        outtime += 5 * 60;
                        break;
                    case "Plus10":
                        outtime += 10 * 60;
                        break;
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    void updateNotification(){
        int sec = (outtime % 60);
        int min = (outtime % 3600) / 60;
        int hour = (outtime / 3600);
        builder.setContentText(String.format("%02d:%02d:%02d", hour, min, sec));
        notificationManager.notify(notificationId, builder.build());
    }

    void finish(){
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        if(timer != null){
            timer.cancel();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        finish();
        super.onDestroy();
    }
}
