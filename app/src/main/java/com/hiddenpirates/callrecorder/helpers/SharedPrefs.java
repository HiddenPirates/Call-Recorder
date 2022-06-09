package com.hiddenpirates.callrecorder.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class SharedPrefs {

    private static final String APP_PREFS_NAME = "CallRecorderPreference";

    private final SharedPreferences mPreference;
    private final SharedPreferences.Editor mPrefEditor;

    public SharedPrefs(Context context) {
        this.mPreference = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        this.mPrefEditor = mPreference.edit();
    }

//__________________________________________________________________________________________________

    public void saveRecordingStartToastBoolean(boolean isOn){
        mPrefEditor.putBoolean("show_start_recording_toast", isOn);
        mPrefEditor.commit();
    }

    public void saveRecordingStopToastBoolean(boolean isOn){
        mPrefEditor.putBoolean("show_stop_recording_toast", isOn);
        mPrefEditor.commit();
    }

    public void saveDarkModeOnOffBoolean(boolean isOn){
        mPrefEditor.putBoolean("is_dark_mode_on", isOn);
        mPrefEditor.commit();
    }

    public void saveCallRecordingEnabledOrNotBoolean(boolean isOn){
        mPrefEditor.putBoolean("is_call_recording_enabled", isOn);
        mPrefEditor.commit();
    }

    public void saveRecordingSavingLocation(String location){
        mPrefEditor.putString("recording_saving_location", location);
        mPrefEditor.commit();
    }

    public void saveSortRecordingOrder(String order){
        mPrefEditor.putString("recording_sorting_order", order);
        mPrefEditor.commit();
    }
//__________________________________________________________________________________________________

    public boolean isStartRecordingToastEnabled(){
        return  mPreference.getBoolean("show_start_recording_toast", true);
    }

    public boolean isStopRecordingToastEnabled(){
        return  mPreference.getBoolean("show_stop_recording_toast", true);
    }

    public boolean isDarkModeEnabled(){
        return  mPreference.getBoolean("is_dark_mode_on", false);
    }

    public boolean isCallRecordingEnabled(){
        return  mPreference.getBoolean("is_call_recording_enabled", true);
    }

    public String getRecordingSavingLocation(){
        return  mPreference.getString("recording_saving_location", Environment.getExternalStorageDirectory().getPath() + "/Call Recorder/");
    }

    public String getRecordingSortOrder(){
        return  mPreference.getString("recording_sorting_order", "Newest");
    }
}
