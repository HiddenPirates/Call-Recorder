package com.hiddenpirates.callrecorder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hiddenpirates.callrecorder.activities.StartForegroundActivity;
import com.hiddenpirates.callrecorder.helpers.ContactsHelper;
import com.hiddenpirates.callrecorder.services.RecordingService;
import com.hiddenpirates.callrecorder.services.MyCallScreeningService;

public class PhoneReceiver extends BroadcastReceiver {

    public static final String TAG = "MADARA";
    private static boolean callAnswered = false;
    private static boolean callRinging = false;
    private static boolean callEnded = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Contact Name: " + ContactsHelper.getContactNameByPhoneNumber(MyCallScreeningService.PHONE_NUMBER, context));

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")){

            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                if (!callAnswered){

                    Intent serviceIntent = new Intent(context, StartForegroundActivity.class);
                    serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(serviceIntent);

                    callAnswered = true;

                    new Handler(Looper.getMainLooper()).postDelayed(() -> callAnswered = false, 2000);

                    Log.d(TAG, "EXTRA_STATE_OFFHOOK/Call_Answered_Or_Outgoing");
                }
            }
            else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                if (!callEnded){

                    context.stopService(new Intent(context, RecordingService.class));
                    callEnded = true;

                    new Handler(Looper.getMainLooper()).postDelayed(() -> callEnded = false, 2000);

                    Log.d(TAG, "EXTRA_STATE_IDLE/Call_Ended");
                }
            }
            else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                if (!callRinging){

                    callRinging = true;

                    new Handler(Looper.getMainLooper()).postDelayed(() -> callRinging = false, 2000);

                    Log.d(TAG, "EXTRA_STATE_RINGING/Call_Ringing");
                }
            }
        }
    }
}
