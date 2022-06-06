package com.hiddenpirates.callrecorder.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hiddenpirates.callrecorder.helpers.SharedPrefs;

import java.util.Objects;

import callrecorder.BuildConfig;
import callrecorder.R;

public class SettingsActivity extends AppCompatActivity {

    CheckBox startToastCB, stopToastCB;
    SwitchCompat darkModeOnOffSwitch, recordingOnOffSwitch;
    CardView recordingSavingLocationPickerCV, recordingSortingOrderCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");

        initVariables();

        Intent intent = getIntent();

        String activity_started_by;

        if (intent.hasExtra("activity_started_by")){
            activity_started_by = intent.getStringExtra("activity_started_by");
        }
        else{
            activity_started_by = "started by other app";
        }

        if (activity_started_by.equalsIgnoreCase(BuildConfig.APPLICATION_ID)){
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
        else{
            toolbar.setNavigationOnClickListener(view -> {
                Intent intent1 = new Intent(SettingsActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();
            });
        }

        SharedPrefs sharedPrefs = new SharedPrefs(SettingsActivity.this);
//..................................................................................................

        startToastCB.setChecked(sharedPrefs.isStartRecordingToastEnabled());
        stopToastCB.setChecked(sharedPrefs.isStopRecordingToastEnabled());
        darkModeOnOffSwitch.setChecked(sharedPrefs.isDarkModeEnabled());
        recordingOnOffSwitch.setChecked(sharedPrefs.isCallRecordingEnabled());
//..................................................................................................

        startToastCB.setOnCheckedChangeListener((compoundButton, isChecked) -> sharedPrefs.saveRecordingStartToastBoolean(isChecked));
        stopToastCB.setOnCheckedChangeListener((compoundButton, isChecked) -> sharedPrefs.saveRecordingStopToastBoolean(isChecked));

        darkModeOnOffSwitch.setOnCheckedChangeListener((compoundButton, isEnabled) -> {

            sharedPrefs.saveDarkModeOnOffBoolean(isEnabled);

            if (isEnabled){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        recordingOnOffSwitch.setOnCheckedChangeListener((compoundButton, isEnabled) -> sharedPrefs.saveCallRecordingEnabledOrNotBoolean(isEnabled));

        recordingSavingLocationPickerCV.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(i, 9999);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9999) {
            Log.d("MADARA", "Result URI " + data.getData());
            Log.d("MADARA", "" + data.getData().getPath());
            Log.d("MADARA", "" + data.getDataString());
        }
    }

    //__________________________________________________________________________________________________

    private void initVariables() {
        startToastCB = findViewById(R.id.startToastCB);
        stopToastCB = findViewById(R.id.stopToastCB);
        darkModeOnOffSwitch = findViewById(R.id.darkModeOnOffSwitch);
        recordingOnOffSwitch = findViewById(R.id.recordingOnOffSwitch);
        recordingSavingLocationPickerCV = findViewById(R.id.recording_saved_location_cardview);
        recordingSortingOrderCV = findViewById(R.id.recording_sort_order_cardview);
    }
}