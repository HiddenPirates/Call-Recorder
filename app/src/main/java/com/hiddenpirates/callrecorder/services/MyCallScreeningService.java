package com.hiddenpirates.callrecorder.services;

import android.telecom.Call;
import android.telecom.CallScreeningService;

import androidx.annotation.NonNull;

public class MyCallScreeningService extends CallScreeningService {

    public static String PHONE_NUMBER = "";

    @Override
    public void onScreenCall(@NonNull Call.Details callDetails) {
        PHONE_NUMBER = callDetails.getHandle().getSchemeSpecificPart();
    }
}