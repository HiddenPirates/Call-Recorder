package com.hiddenpirates.callrecorder.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hiddenpirates.callrecorder.helpers.CustomFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import callrecorder.R;

public class RVAdapterFileList extends RecyclerView.Adapter<RVAdapterFileList.MyCustomViewHolder> implements Filterable {

    Context context;
    JSONArray fileInfos;
    JSONArray fileInfos2;

    public RVAdapterFileList(Context context, JSONArray fileInfos){
        this.context = context;
        this.fileInfos = fileInfos;
        fileInfos2 = fileInfos;
    }

    @NonNull
    @Override
    public MyCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sample_rv_layout_main_activity, parent, false);
        return new MyCustomViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyCustomViewHolder holder, int position) {
        try {
            holder.fileNameTV.setText(fileInfos.getJSONObject(holder.getAdapterPosition()).getString("name"));

            String fileSizeAndDate = fileInfos.getJSONObject(holder.getAdapterPosition()).get("modified_date") + "\t\t (" + fileInfos.getJSONObject(holder.getAdapterPosition()).get("size") + ")";

            holder.fileInfoTV.setText(fileSizeAndDate);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

//        ------------------------------------------------------------------------------------------
        holder.itemView.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(fileInfos.getJSONObject(holder.getAdapterPosition()).getString("absolute_path")));
                intent.setDataAndType(uri, "audio/mpeg");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
                Log.d("MADARA", e.getMessage());
            }
        });
//        ------------------------------------------------------------------------------------------
        holder.itemView.setOnLongClickListener(view -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_control_btns);
            bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

            bottomSheetDialog.findViewById(R.id.playBtnCV).setOnClickListener(view1 -> {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(fileInfos.getJSONObject(holder.getAdapterPosition()).getString("absolute_path")));
                    intent.setDataAndType(uri, "audio/mpeg");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.d("MADARA", e.getMessage());
                }
            });

            bottomSheetDialog.findViewById(R.id.deleteBtnCV).setOnClickListener(view1 -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Delete");
                builder.setMessage("Do you really want to delete?");
                builder.setIcon(R.drawable.ic_delete_24);
                builder.setPositiveButton("Delete", (dialogInterface, i) -> {
                    try {

                        File temp_file = new File(fileInfos.getJSONObject(holder.getAdapterPosition()).get("absolute_path").toString());

                        if (temp_file.delete()){

                            Toast.makeText(context, "Recorded file deleted successfully.", Toast.LENGTH_SHORT).show();

                            fileInfos.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();
                            notifyItemRemoved(holder.getAdapterPosition());

                            bottomSheetDialog.dismiss();
                        }
                        else{
                            Toast.makeText(context, "Failed to delete file.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();
            });


            bottomSheetDialog.findViewById(R.id.renameBtnCV).setOnClickListener(view1 -> Toast.makeText(context, "This button is not working currently. " + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show());

            bottomSheetDialog.show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return fileInfos.length();
    }

    @Override
    public Filter getFilter() {
        return filesFilter;
    }

    private final Filter filesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            JSONArray filteredFileInfos = new JSONArray();

            if (charSequence == null || charSequence.length() == 0){
                filteredFileInfos = fileInfos2;
            }
            else{
                String search_term = charSequence.toString().trim();

                for (int i = 0; i < fileInfos2.length(); i++){

                    try {
                        if (fileInfos2.getJSONObject(i).getString("name").toLowerCase().contains(search_term) || fileInfos2.getJSONObject(i).getString("modified_date").toLowerCase().contains(search_term)){
                            filteredFileInfos.put(fileInfos2.getJSONObject(i));
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredFileInfos;
            filterResults.count = filteredFileInfos.length();

            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            fileInfos = (JSONArray) filterResults.values;
            notifyDataSetChanged();
        }
    };

    public static class MyCustomViewHolder extends RecyclerView.ViewHolder{

        TextView fileNameTV, fileInfoTV;

        public MyCustomViewHolder(View itemView) {
            super(itemView);

            fileNameTV = itemView.findViewById(R.id.fileNameTV);
            fileInfoTV = itemView.findViewById(R.id.fileInfoTV);
        }
    }
}
