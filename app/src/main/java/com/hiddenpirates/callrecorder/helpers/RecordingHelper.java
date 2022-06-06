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
    public static MediaRecorder recorder;

//__________________________________________________________________________________________________

    public RecordingHelper(Context context, String phoneNumber){
        this.context = context;
        this.phoneNumber = phoneNumber;
    }
//__________________________________________________________________________________________________

    @SuppressLint("SimpleDateFormat")
    public void startVoiceRecoding() {

        if (checkStorageAccessPermission()) {

            String app_content_directory_name = "/Call Recorder/";

            File dir = new File(Environment.getExternalStorageDirectory().getPath() + app_content_directory_name);

            if (!dir.exists()) {
                if (!dir.mkdirs()){
                    Toast.makeText(context, "Failed to create directory.", Toast.LENGTH_SHORT).show();
                }
            }

            String directoryPath = Environment.getExternalStorageDirectory().getPath() + app_content_directory_name;

            String fileName = ContactsHelper.getContactNameByPhoneNumber(phoneNumber, context)
                    + "_("
                    + phoneNumber
                    + ")_"
                    + new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(Calendar.getInstance().getTime())
                    + ".m4a";

            finalFileName = directoryPath + fileName;

            recorder = new MediaRecorder();
            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioEncodingBitRate(16);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(finalFileName);

            try {
                recorder.prepare();
                recorder.start();

                if (new SharedPrefs(context).isStartRecordingToastEnabled()){
                    Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Recording start failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "startVoiceRecoding: " + e.getMessage());
            }
        }
        else {
            Toast.makeText(context, "Storage permission is not granted.", Toast.LENGTH_SHORT).show();
        }
    }
//--------------------------------------------------------------------------------------------------

    public void stopVoiceRecoding() {

        try {
            recorder.release();
            recorder.stop();
            recorder.reset();
            recorder = null;

            if (new SharedPrefs(context).isStopRecordingToastEnabled()){
                Toast.makeText(context, "Recording saved to: " + finalFileName, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            recorder = null;

            if (new SharedPrefs(context).isStopRecordingToastEnabled()){
                Toast.makeText(context, "Recording saved", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "stopVoiceRecoding: " + e.getMessage());
        }
    }

//__________________________________________________________________________________________________

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
