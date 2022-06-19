package com.hiddenpirates.callrecorder.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.hiddenpirates.callrecorder.activities.settingspages.SettingsActivity;
import com.hiddenpirates.callrecorder.adapters.RVAdapterFileList;
import com.hiddenpirates.callrecorder.helpers.CustomFunctions;
import com.hiddenpirates.callrecorder.helpers.SharedPrefs;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import callrecorder.BuildConfig;
import callrecorder.R;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 4528;
    private static final int CALL_SCREEN_REQUEST_ID = 64543;
    private static final int MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 5000;

    private boolean doubleBackPressed = false;

    private LinearLayout emptyFileIconContainer, fileLoadingInfoContainer;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView allFilesRecyclerView;
    private RVAdapterFileList rvAdapterFileList;
    private FloatingActionButton scrollBackToTopBtn, scrollToBottomBtn;
    private TextView totalFileLoadedTv;
    private CardView openInFileManagerCV;


    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//--------------------------------------------------------------------------------------------------
        if (new SharedPrefs(MainActivity.this).getAppearanceValue().equalsIgnoreCase(getString(R.string.dark_mode))){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if (new SharedPrefs(MainActivity.this).getAppearanceValue().equalsIgnoreCase(getString(R.string.light_mode))){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
//--------------------------------------------------------------------------------------------------

        getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.app_name));

        initVariables();

//--------------------------------------------------------------------------------------------------

        View headView = navigationView.getHeaderView(0);

        ((TextView) headView.findViewById(R.id.header_layout_version_tv)).setText("Version: " + BuildConfig.VERSION_NAME);

        Button updateBtnInHeaderLayout = headView.findViewById(R.id.updateBtnInHeaderLayout);

        CustomFunctions.checkForUpdateOnStartApp(this, updateBtnInHeaderLayout);

        updateBtnInHeaderLayout.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HiddenPirates/Call-Recorder/releases"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

//--------------------------------------------------------------------------------------------------

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                CustomFunctions.hideKeyboard(MainActivity.this, drawerView);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                CustomFunctions.hideKeyboard(MainActivity.this, drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent1;

            if (item.getItemId() == R.id.check_update_action) {

                Menu menuNav = navigationView.getMenu();
                MenuItem checkUpdateItem = menuNav.findItem(R.id.check_update_action);
                checkUpdateItem.setEnabled(false);
                checkUpdateItem.setTitle("Checking for new update");

                Toast.makeText(MainActivity.this, "Checking for new update!", Toast.LENGTH_LONG).show();
                CustomFunctions.checkForUpdate(this, checkUpdateItem);
                return true;

            }
            else if (item.getItemId() == R.id.donate_me_action) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.hiddenpirates.com")));
                return true;

            } else if (item.getItemId() == R.id.send_mail_action) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HiddenPirates/Call-Recorder/issues/")));
                return true;

            } else if (item.getItemId() == R.id.share_app_action) {

                intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_sharing_message) + "\n https://github.com/HiddenPirates/Call-Recorder/releases/");
                startActivity(Intent.createChooser(intent1, "Share via"));
                return true;

            } else if (item.getItemId() == R.id.more_app_action) {

                intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5002650060821952731"));
                startActivity(intent1);
                return true;

            } else if (item.getItemId() == R.id.visitWeb_app_action) {

                intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hiddenpirates.com"));
                startActivity(intent1);
                return true;

            } else if (item.getItemId() == R.id.visitGitHub_app_action) {
                intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/HiddenPirates"));
                startActivity(intent1);
                return true;

            } else if (item.getItemId() == R.id.visitFb_app_action) {

                intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/TeamHiddenPirates"));
                startActivity(intent1);
                return true;

            } else {
                return false;
            }
        });

//        ------------------------------------------------------------------------------------------
//
        if (!CustomFunctions.isSystemApp(this)){

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
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tutorial_post_link))));
                    new Handler(Looper.getMainLooper()).postDelayed(this::finishAndRemoveTask, 2000);
                }
                catch (Exception e){
                    Toast.makeText(this, "No app found to open this link", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    onBackPressed();
                }
//                finishAndRemoveTask();
            });
            builder.create();
            builder.show();
        }
        else{
            requestCallScreenPermission();

            if (!isPermissionGranted()){
                askPermission();
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()){
                        requestAllFileAccessPermission();
                    }
                }

                @SuppressLint("SdCardPath")
                File path = new File("/sdcard/Call Recorder/");
                File[] files = path.listFiles();

                Log.d("Madara", files.length + "");

                JSONArray allFilesInformation = new JSONArray();

                if (files != null) {

                    emptyFileIconContainer.setVisibility(View.GONE);
                    allFilesRecyclerView.setVisibility(View.VISIBLE);
                    fileLoadingInfoContainer.setVisibility(View.VISIBLE);


                    Handler handler = new Handler();


                    new Thread(() -> {

                        String sortOrder = new SharedPrefs(MainActivity.this).getRecordingSortOrder();

                        if (sortOrder.equalsIgnoreCase(getString(R.string.sort_by_name_ascending))){
                            CustomFunctions.sortFilesByNameAscending(files);
                        }
                        else if (sortOrder.equalsIgnoreCase(getString(R.string.sort_by_name_descending))){
                            CustomFunctions.sortFilesByNameDescending(files);
                        }
                        else if (sortOrder.equalsIgnoreCase(getString(R.string.sort_by_new))){
                            CustomFunctions.sortNewestFilesFirst(files);
                        }
                        else if (sortOrder.equalsIgnoreCase(getString(R.string.sort_by_old))){
                            CustomFunctions.sortOldestFilesFirst(files);
                        }
                        else{
                            CustomFunctions.sortNewestFilesFirst(files);
                        }

                        int i = 1;

                        for (File file : files) {

                            int finalI = i;

                            handler.post(() -> totalFileLoadedTv.setText("Loading files " + finalI + "/" + files.length));

                            i++;

                            if (file.isFile() && FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("m4a") && file.length() > 0){

                                JSONObject fileInfo = new JSONObject();

                                try {
                                    fileInfo.put("name", file.getName());
                                    fileInfo.put("size", CustomFunctions.fileSizeFormatter(file.length()));
                                    fileInfo.put("modified_date", CustomFunctions.timeFormatter(file.lastModified()));
                                    fileInfo.put("absolute_path", file.getAbsolutePath());

                                    allFilesInformation.put(fileInfo);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        handler.post(() -> {

                            fileLoadingInfoContainer.setVisibility(View.GONE);

                            rvAdapterFileList = new RVAdapterFileList(MainActivity.this, allFilesInformation);
                            allFilesRecyclerView.setAdapter(rvAdapterFileList);

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                            allFilesRecyclerView.setLayoutManager(linearLayoutManager);
                            allFilesRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        });

                    }).start();



//                  33333333333333333333333333333333333333333333333333333333333333333333333333333333

                    openInFileManagerCV.setOnClickListener(view -> {

                        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/Call Recorder/");
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(selectedUri, "*/*");
                        startActivity(intent);
                    });

//                  33333333333333333333333333333333333333333333333333333333333333333333333333333333

                    scrollBackToTopBtn.setOnClickListener(view -> allFilesRecyclerView.scrollToPosition(0));
                    scrollToBottomBtn.setOnClickListener(view -> allFilesRecyclerView.scrollToPosition(allFilesRecyclerView.getAdapter().getItemCount() - 1));

                    allFilesRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                            if (newState == RecyclerView.SCROLL_STATE_IDLE) { // No scrolling
                                new Handler().postDelayed(() -> scrollBackToTopBtn.setVisibility(View.GONE), 2000); // delay of 2 seconds before hiding the fab
                                new Handler().postDelayed(() -> scrollToBottomBtn.setVisibility(View.GONE), 2000); // delay of 2 seconds before hiding the fab
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                            if (dy > 0) { // scrolling down
                                scrollBackToTopBtn.setVisibility(View.GONE);
                                scrollToBottomBtn.setVisibility(View.VISIBLE);
                            }
                            else if (dy < 0) { // scrolling up
                                scrollBackToTopBtn.setVisibility(View.VISIBLE);
                                scrollToBottomBtn.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }

        }
//        ------------------------------------------------------------------------------------------
    }

//__________________________________________________________________________________________________

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dropdown_right_corner, menu);

        MenuItem searchBtn = menu.findItem(R.id.menu_search_action);

        SearchView searchView = (SearchView) searchBtn.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search recordings...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rvAdapterFileList.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setOnFocusChangeListener((view, b) -> {
            CustomFunctions.hideKeyboard(MainActivity.this, view);
            searchView.clearFocus();
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_settings_action){

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("activity_started_by", BuildConfig.APPLICATION_ID);
            startActivity(intent);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    //__________________________________________________________________________________________________
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            if (doubleBackPressed) {
                super.onBackPressed();
            }
            else {
                this.doubleBackPressed = true;
                Snackbar.make(drawerLayout, "Double back press to exit.", Snackbar.LENGTH_LONG).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackPressed = false, 2000);
            }
        }
    }
//__________________________________________________________________________________________________

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

// _________________________________________________________________________________________________
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

//_________________________________________________________________________________________________
//_________________________________________________________________________________________________

    private boolean isPermissionGranted() {

        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
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

    private void requestAllFileAccessPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
        }
    }

    private void requestCallScreenPermission(){
        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, CALL_SCREEN_REQUEST_ID);
    }

    private void initVariables(){
        emptyFileIconContainer = findViewById(R.id.empty_file_icon_container);
        fileLoadingInfoContainer = findViewById(R.id.file_loading_info_container);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_drawer);
        allFilesRecyclerView = findViewById(R.id.allFilesRecyclerView);
        scrollBackToTopBtn = findViewById(R.id.scrollBackToTopBtn);
        scrollToBottomBtn = findViewById(R.id.scrollToBottomBtn);
        totalFileLoadedTv = findViewById(R.id.total_file_loaded_tv);
        openInFileManagerCV = findViewById(R.id.openInFileManagerCV);
    }
}