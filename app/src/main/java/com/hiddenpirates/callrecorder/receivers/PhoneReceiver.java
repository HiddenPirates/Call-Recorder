package com.hiddenpirates.callrecorder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.hiddenpirates.callrecorder.activities.StartForegroundActivity;
import com.hiddenpirates.callrecorder.helpers.SharedPrefs;
import com.hiddenpirates.callrecorder.services.RecordingService;

public class PhoneReceiver extends BroadcastReceiver {

    private static boolean callAnswered = false;
    private static boolean callRinging = false;
    private static boolean callEnded = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")){

            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                if (!callAnswered){

                    if (new SharedPrefs(context).isCallRecordingEnabled()){
                        Intent serviceIntent = new Intent(context, StartForegroundActivity.class);
                        serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(serviceIntent);
                    }

                    callAnswered = true;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> callAnswered = false, 2000);
                }
            }
            else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                if (!callEnded){

                    if (new SharedPrefs(context).isCallRecordingEnabled()){
                        context.stopService(new Intent(context, RecordingService.class));
                    }

                    callEnded = true;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> callEnded = false, 2000);
                }
            }
            else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                if (!callRinging){

                    callRinging = true;
                    new Handler(Looper.getMainLooper()).postDelayed(() -> callRinging = false, 2000);
                }
            }
        }
    }
}
