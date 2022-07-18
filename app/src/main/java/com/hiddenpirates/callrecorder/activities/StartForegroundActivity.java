package com.hiddenpirates.callrecorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hiddenpirates.callrecorder.R;
import com.hiddenpirates.callrecorder.services.RecordingService;

public class StartForegroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_forground);

        Log.d("MADARA", "onCreate: " + 4555);

        findViewById(R.id.exitAppBtn).setOnClickListener(view -> finishAndRemoveTask());

        ContextCompat.startForegroundService(this, new Intent(this, RecordingService.class));
        finishAndRemoveTask();
    }
}