package com.hiddenpirates.callrecorder.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.comparator.NameFileComparator;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import callrecorder.BuildConfig;
import callrecorder.R;

public class CustomFunctions {

//__________________________________________________________________________________________________
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
//__________________________________________________________________________________________________

    public static boolean isSystemApp(Context context){

        boolean isSystemApp = false;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(0);

        for (ApplicationInfo ai: installedApps) {

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                if (ai.packageName.equalsIgnoreCase(context.getPackageName())){
                    isSystemApp = true;
                }
            }
        }

        return isSystemApp;
    }
//__________________________________________________________________________________________________

    public static void sortOldestFilesFirst(File[] files) {
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
    }

    public static void sortNewestFilesFirst(File[] files) {
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
    }

    public static void sortFilesByNameAscending(File[] files){
        Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);
    }

    public static void sortFilesByNameDescending(File[] files){
        Arrays.sort(files, NameFileComparator.NAME_REVERSE);
    }
//__________________________________________________________________________________________________
    public static String fileSizeFormatter(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
//__________________________________________________________________________________________________

    public static String timeFormatter(Long time) {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss");
        return dateFormat.format(time);
    }
//__________________________________________________________________________________________________

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        DecimalFormat deciFormat = new DecimalFormat();
        deciFormat.setMaximumFractionDigits(places);
        return Double.parseDouble(deciFormat.format(value));
    }
//__________________________________________________________________________________________________

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void checkForUpdate(Context context, MenuItem menuItem) {

        double nextVersion = Double.parseDouble(BuildConfig.VERSION_NAME) + 0.1;
        String vTag = "v" + round(nextVersion, 1);

        Log.d("MADARA", "checkForUpdate: " + vTag);

        new Thread(() -> {
            try {
                String document_title = Jsoup.connect("https://github.com/HiddenPirates/Call-Recorder/releases/tag/" + vTag)
                        .timeout(30000).get().title();

                Log.d("MADARA", "checkForUpdate: " + document_title);

                ((AppCompatActivity) context).runOnUiThread(() -> {

                    menuItem.setEnabled(true);
                    menuItem.setTitle("Check New Update");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getResources().getString(R.string.app_name));
                    builder.setMessage("New update found!");
                    builder.setCancelable(false);
                    builder.setIcon(context.getResources().getDrawable(R.drawable.nav_header_img));
                    builder.setPositiveButton("Update", (dialog, which) -> {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HiddenPirates/Call-Recorder/releases/tag/" + vTag)));
                        dialog.dismiss();
                    });

                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                    AlertDialog alert = builder.create();
                    alert.show();

                });
            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("MADARA", "checkForUpdate: " + e.getMessage());

                ((AppCompatActivity) context).runOnUiThread(() -> {

                    menuItem.setEnabled(true);
                    menuItem.setTitle("Check New Update");
                    simpleAlert(context, "No update found", "You're using the latest version.", "Dismiss", context.getResources().getDrawable(R.drawable.done_icon));
                });
            }
        }).start();
    }

//    ----------------------------------------------------------------------------

    public static void simpleAlert(Context context, String title, String message, String dismissBtn, Drawable drawableIcon) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (title != null) {
            builder.setTitle(title);
        }

        if (message != null) {
            builder.setMessage(message);
        }

        if (drawableIcon != null) {
            builder.setIcon(drawableIcon);
        }

        builder.setCancelable(false);

        builder.setPositiveButton(dismissBtn, (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();

        alert.show();
    }
//    ----------------------------------------------------------------------------
}
