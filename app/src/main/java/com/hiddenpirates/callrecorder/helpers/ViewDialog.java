package com.hiddenpirates.callrecorder.helpers;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import callrecorder.R;

public class ViewDialog {

    public void showRenameFileInputDialog(Context context, String oldName, String filePath) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_rename_file);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        EditText newNameET = dialog.findViewById(R.id.new_file_name_et);

        newNameET.setText(oldName.substring(0, oldName.length() - 4));

        dialog.findViewById(R.id.rename_cancel_btn).setOnClickListener(view -> dialog.dismiss());

        dialog.findViewById(R.id.rename_confirm_btn).setOnClickListener(view -> {

            String newName = newNameET.getText().toString().trim();

            if (!newName.equalsIgnoreCase("")){

                newName += ".m4a";

                File oldNameFile = new File(new File(filePath).getParent().concat("/" + oldName));
                File newNameFile = new File(new File(filePath).getParent().concat("/" + newName));

                Log.d("MADARA", newName);

                if (oldNameFile.renameTo(newNameFile)){
                    Toast.makeText(context, "File renamed.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
            else{
                Toast.makeText(context, "Please provide a valid name.", Toast.LENGTH_SHORT).show();
            }

        });

        dialog.show();
    }
}
