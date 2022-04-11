package com.hiddenpirates.callrecorder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.UUID;

import callrecorder.R;

public class MainActivity extends AppCompatActivity {

    Button startRecordButton, stopRecordButton;
    private static final String TAG = "MADARA";
    private static final int REQUEST_PERMISSION_CODE = 4528;
    private static final int MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 5000;
    private static String fileName = null;
    private MediaRecorder recorder = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        askPermission();

        startRecordButton.setOnClickListener(v -> startVoiceRecoding());

        stopRecordButton.setOnClickListener(v -> stopVoiceRecoding());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()){
                    Toast.makeText(this, "Please allow access of this device's external storage", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length >= 3
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED
        ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()){

                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
                }
            }
        }
        else{
            Toast.makeText(this, "Please allow all permission", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS));
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE
        }, REQUEST_PERMISSION_CODE);

    }

    private void stopVoiceRecoding() {

        try {
            recorder.release();
            recorder.stop();
            recorder.reset();
            recorder = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        startRecordButton.setVisibility(View.VISIBLE);
        stopRecordButton.setVisibility(View.GONE);

        Toast.makeText(this, "Recording saved to " + fileName, Toast.LENGTH_SHORT).show();
    }

    private void startVoiceRecoding() {

        fileName = Environment.getExternalStorageDirectory().getPath() + "/" + UUID.randomUUID() + ".mp3";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(fileName);

        try {
            recorder.prepare();
            recorder.start();

            startRecordButton.setVisibility(View.GONE);
            stopRecordButton.setVisibility(View.VISIBLE);

            Toast.makeText(MainActivity.this, "Recording started!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startVoiceRecoding: error");
            Toast.makeText(MainActivity.this, e.getMessage()+"\n"+e.getLocalizedMessage() , Toast.LENGTH_SHORT).show();
            Log.e(TAG,  e.getMessage()+"\n"+e.getLocalizedMessage());
        }

    }

    private void initializeComponents() {
        startRecordButton = findViewById(R.id.startRecordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
    }
}