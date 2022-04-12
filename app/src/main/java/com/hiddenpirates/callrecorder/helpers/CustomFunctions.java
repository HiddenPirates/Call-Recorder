package com.hiddenpirates.callrecorder.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Objects;

import callrecorder.BuildConfig;
import callrecorder.R;

public class CustomFunctions {

//    -----------------------------------------------------------------

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void checkForUpdate(Context context, MenuItem menuItem) {

        new Thread(() -> {

            String latestVersion;
            String currentVersion;

            try {
                latestVersion = Objects.requireNonNull(Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                        .timeout(30000).get()
                        .select("div.hAyfc:nth-child(4)>span:nth-child(2) > div:nth-child(1)> span:nth-child(1)").first()).ownText();

                if (!latestVersion.equals("")) {

                    currentVersion = BuildConfig.VERSION_NAME;

                    double cVersion = Double.parseDouble(currentVersion);
                    double lVersion = Double.parseDouble(latestVersion);

                    if (lVersion > cVersion) {

                        ((AppCompatActivity) context).runOnUiThread(() -> {

                            menuItem.setEnabled(true);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getResources().getString(R.string.app_name));
                            builder.setMessage("New update found!");
                            builder.setCancelable(false);
                            builder.setIcon(context.getResources().getDrawable(R.drawable.ic_get_app));
                            builder.setPositiveButton("Update", (dialog, which) -> {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                                dialog.dismiss();
                            });

                            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                            AlertDialog alert = builder.create();
                            alert.show();

                        });
                    } else {

                        ((AppCompatActivity) context).runOnUiThread(() -> {

                            menuItem.setEnabled(true);
                            simpleAlert(context, "No update found", "You're using the latest version.", "Dismiss", context.getResources().getDrawable(R.drawable.ic_check_circle));
                        });
                    }
                }
            } catch (IOException e) {

                ((AppCompatActivity) context).runOnUiThread(() -> {

                    menuItem.setEnabled(true);
                    simpleAlert(context, "Error", "Failed to check for new update.", "Dismiss", context.getResources().getDrawable(R.drawable.ic_error));
                });

                e.printStackTrace();
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
