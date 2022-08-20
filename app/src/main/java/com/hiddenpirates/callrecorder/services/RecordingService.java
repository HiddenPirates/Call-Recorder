package com.hiddenpirates.callrecorder.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hiddenpirates.callrecorder.BuildConfig;
import com.hiddenpirates.callrecorder.R;
import com.hiddenpirates.callrecorder.helpers.RecordingHelper;
import com.hiddenpirates.callrecorder.helpers.SharedPrefs;
import com.hiddenpirates.callrecorder.receivers.ActionReceiver;


public class RecordingService extends Service {

    public static final String CHANNEL_ID = BuildConfig.APPLICATION_ID;
    RecordingHelper recordingHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();

        Intent stopRecordingIntent = new Intent(this, ActionReceiver.class);
        stopRecordingIntent.putExtra("stopRecording", "YES");

        PendingIntent stopRecordingPendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            stopRecordingPendingIntent = PendingIntent.getBroadcast(this, 1, stopRecordingIntent, PendingIntent.FLAG_MUTABLE);
        }
        else {
            stopRecordingPendingIntent = PendingIntent.getBroadcast(this, 1, stopRecordingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder.setContentTitle("Call Recorder");
        notificationBuilder.setContentText("Call recording in progress");
        notificationBuilder.setSmallIcon(R.drawable.ic_keyboard_voice);
        notificationBuilder.setOngoing(true);

        if (!new SharedPrefs(this).isStopRecordingNotificationButtonHidden()){
            notificationBuilder.addAction(R.drawable.ic_stop_circle_24, "Stop", stopRecordingPendingIntent);
        }


        Notification notification = notificationBuilder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        }
        else{
            startForeground(1, notification);
        }

        recordingHelper = new RecordingHelper(this, MyCallScreeningService.PHONE_NUMBER);
        recordingHelper.startVoiceRecoding();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        recordingHelper.stopVoiceRecoding();
        super.onDestroy();
    }

    private void createNotificationChannel() {

        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Call Recorder Service Channel",
                NotificationManager.IMPORTANCE_NONE
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

}
