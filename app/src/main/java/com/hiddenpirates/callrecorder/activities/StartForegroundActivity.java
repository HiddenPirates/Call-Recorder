package com.hiddenpirates.callrecorder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import com.hiddenpirates.callrecorder.services.RecordingService;

import callrecorder.R;

public class StartForegroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_forground);

        findViewById(R.id.exitAppBtn).setOnClickListener(view -> finishAndRemoveTask());

        ContextCompat.startForegroundService(this, new Intent(this, RecordingService.class));
        finishAndRemoveTask();
    }
}