package com.hiddenpirates.callrecorder.activities.settingspages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.hiddenpirates.callrecorder.activities.MainActivity;
import com.hiddenpirates.callrecorder.helpers.SharedPrefs;

import java.util.Objects;

import callrecorder.BuildConfig;
import callrecorder.R;

public class SettingsActivity extends AppCompatActivity {

    CheckBox startToastCB, stopToastCB;
    SwitchCompat darkModeOnOffSwitch, recordingOnOffSwitch;
    CardView recordingSortingOrderCV;
    TextView savedSortByNameTV;

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
        savedSortByNameTV.setText(sharedPrefs.getRecordingSortOrder());
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
//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
        recordingOnOffSwitch.setOnCheckedChangeListener((compoundButton, isEnabled) -> sharedPrefs.saveCallRecordingEnabledOrNotBoolean(isEnabled));
//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,

        recordingSortingOrderCV.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(SettingsActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.sort_by_popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                sharedPrefs.saveSortRecordingOrder(menuItem.getTitle().toString());
                savedSortByNameTV.setText(menuItem.getTitle().toString());
                Toast.makeText(SettingsActivity.this, "Restart the app to see effect.", Toast.LENGTH_LONG).show();
                return true;
            });
            popupMenu.show();
        });
//,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //__________________________________________________________________________________________________

    private void initVariables() {
        startToastCB = findViewById(R.id.startToastCB);
        stopToastCB = findViewById(R.id.stopToastCB);
        darkModeOnOffSwitch = findViewById(R.id.darkModeOnOffSwitch);
        recordingOnOffSwitch = findViewById(R.id.recordingOnOffSwitch);
        recordingSortingOrderCV = findViewById(R.id.recording_sort_order_cardview);
        savedSortByNameTV = findViewById(R.id.savedSortByNameTV);
    }
}