package com.hiddenpirates.callrecorder.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecordingHelper {

    private static final String TAG = "MADARA";
    Context context;
    private final String phoneNumber;
    String finalFileName;
    private MediaRecorder recorder = new MediaRecorder();

    public RecordingHelper(Context context, String phoneNumber){
        this.context = context;
        this.phoneNumber = phoneNumber;
    }

    @SuppressLint("SimpleDateFormat")
    public void startVoiceRecoding() {

        if (checkStorageAccessPermission()) {

            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Call-Recorder");

            if (!dir.exists()) {
                if (!dir.mkdirs()){
                    Toast.makeText(context, "Failed to create directory.", Toast.LENGTH_SHORT).show();
                }
            }

            String directoryPath = Environment.getExternalStorageDirectory().getPath() + "/Call-Recorder/";

            String fileName = ContactsHelper.getContactNameByPhoneNumber(phoneNumber, context)
                    + "_"
                    + phoneNumber
                    + ")_"
                    + new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(Calendar.getInstance().getTime())
                    + ".mp3";

            finalFileName = directoryPath + fileName;

//            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(finalFileName);

            try {
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Recording start failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "stopVoiceRecoding: " + e.getMessage());
            }

        } else {
            Toast.makeText(context, "Storage permission is not granted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopVoiceRecoding() {

        try {
            recorder.release();
            recorder.stop();
            recorder.reset();
            recorder = null;
            Toast.makeText(context, "Recording saved to: " + finalFileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Recording saved to:> " + finalFileName, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "stopVoiceRecoding: " + e.getMessage());
        }
    }

    private boolean checkStorageAccessPermission() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return Environment.isExternalStorageManager();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
