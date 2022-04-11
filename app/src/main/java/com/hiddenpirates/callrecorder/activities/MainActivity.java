package com.hiddenpirates.callrecorder.activities;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hiddenpirates.callrecorder.helpers.RecordingHelper;
import com.hiddenpirates.callrecorder.services.MyCallScreeningService;

import callrecorder.R;

public class MainActivity extends AppCompatActivity {

    Button startRecordButton, stopRecordButton;

    private static final int REQUEST_PERMISSION_CODE = 4528;
    private static final int CALL_SCREEN_REQUEST_ID = 64543;
    private static final int MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 5000;

    RecordingHelper recordingHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        requestCallScreenPermission();
        askPermission();

        if (RecordingHelper.recorder != null){
            stopRecordButton.setVisibility(View.VISIBLE);
            startRecordButton.setVisibility(View.GONE);
        }
        else{
            stopRecordButton.setVisibility(View.GONE);
            startRecordButton.setVisibility(View.VISIBLE);
        }

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

    private void requestCallScreenPermission(){
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, CALL_SCREEN_REQUEST_ID);
    }

    private void startVoiceRecoding() {
        recordingHelper = new RecordingHelper(this, MyCallScreeningService.PHONE_NUMBER);
        recordingHelper.startVoiceRecoding();
    }

    private void stopVoiceRecoding() {
        recordingHelper.stopVoiceRecoding();
    }

    private void initializeComponents() {
        startRecordButton = findViewById(R.id.startRecordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
    }
}