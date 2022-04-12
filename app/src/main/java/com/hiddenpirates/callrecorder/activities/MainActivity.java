package com.hiddenpirates.callrecorder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hiddenpirates.callrecorder.helpers.RecordingHelper;
import com.hiddenpirates.callrecorder.services.MyCallScreeningService;
import com.hiddenpirates.callrecorder.services.RecordingService;

import java.util.List;

import callrecorder.R;

public class MainActivity extends AppCompatActivity {

    public Button startRecordButton, stopRecordButton;

    private static final int REQUEST_PERMISSION_CODE = 4528;
    private static final int CALL_SCREEN_REQUEST_ID = 64543;
    private static final int MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 5000;

    RecordingHelper recordingHelper;

    boolean isSystemApp = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ------------------------------------------------------------------------------------------
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(0);

        for (ApplicationInfo ai: installedApps) {

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                if (ai.packageName.equalsIgnoreCase(getPackageName())){
                    isSystemApp = true;
                }
            }
        }

        if (!isSystemApp){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.not_system_app_message_title));
            builder.setMessage(getString(R.string.not_system_app_message_body));
            builder.setIcon(R.drawable.ic_error);
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", (dialog, which) -> {
                dialog.dismiss();
                finishAndRemoveTask();
            });
            builder.setNegativeButton("Read Post",  (dialog, which) -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_name))));
                } catch (Exception e){
                    Toast.makeText(this, "No app found to open this link", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                finishAndRemoveTask();
            });
            builder.create();
            builder.show();
        }
        else{
            requestCallScreenPermission();
            askPermission();
        }
//        ------------------------------------------------------------------------------------------
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
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
                    requestAllFileAccessPermission();
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

    private void requestAllFileAccessPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
        }
    }
}