package com.hiddenpirates.callrecorder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hiddenpirates.callrecorder.services.RecordingService;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle intentExtras = intent.getExtras();

        if (intentExtras.containsKey("stopRecording")){
            context.stopService(new Intent(context, RecordingService.class));
        }
    }
}
