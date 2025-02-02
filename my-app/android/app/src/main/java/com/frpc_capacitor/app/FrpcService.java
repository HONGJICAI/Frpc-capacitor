package com.frpc_capacitor.app;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import java.io.File;
import java.io.IOException;

public class FrpcService extends Service {
    private static final String CHANNEL_ID = "FrpcServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private Process process;
    private String binFilePath;
    private String configPath;
    private String logPath;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        binFilePath = intent.getStringExtra("binFilePath");
        configPath = intent.getStringExtra("configPath");
        logPath = intent.getStringExtra("logPath");

        var ok = startFrpcProcess();
        if (!ok) {
            return START_NOT_STICKY;
        }

        var notification = createNotification();
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        return START_STICKY;
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Frpc Service Channel",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Frpc Service")
                .setContentText("Frpc is running")
                .setSmallIcon(android.R.drawable.ic_menu_manage)
                .setPriority(2);

        return builder.build();
    }

    // TODO: check if the process is still running
    private boolean startFrpcProcess() {
        try {
            ProcessBuilder pb = new ProcessBuilder(binFilePath, "-c", configPath)
                    .redirectOutput(new File(logPath));
            process = pb.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            process = null;
            return false;
        }
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
        if (process != null) {
            process.destroy();
        }
    }
}
