package com.rk.applock.launcher;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rk.applock.MainActivity;
import com.rk.applock.R;

import java.util.ArrayList;

public class LauncherActivity extends AppCompatActivity implements ViewNavigator {

    private static final String TAG = "LauncherActivity";
    private GridView gridview;


    private ImageView menu_button;
    private ImageView menu_setting;

    private ConstraintLayout front_cover;
    private LauncherPresenter presenter;
    private LauncherAdapter adapter;
    private ImageView menu_call, menu_camera, menu_msg, menu_standby;
    private ActivityManager activityManager;
    private LinearLayout lin_Search;
    private EditText ed_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.activity_launcher);

        init_View();
        presenter = new LauncherPresenter(this, this);
        presenter.setAllAppList();
        presenter.checkIsAdminEnabled();
        setwallpaper();
        listener();
        activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
//        presenter.cosuLock();
//        presenter.set_device_admin();
    }


    private void init_View() {
        front_cover = (ConstraintLayout) findViewById(R.id.front_cover);
        gridview = (GridView) findViewById(R.id.gridview);
        menu_button = (ImageView) findViewById(R.id.menu_button);
        menu_setting = (ImageView) findViewById(R.id.menu_setting);
        menu_call = (ImageView) findViewById(R.id.menu_call);
        menu_camera = (ImageView) findViewById(R.id.menu_camera);
        menu_standby = (ImageView) findViewById(R.id.menu_standby);
        menu_msg = (ImageView) findViewById(R.id.menu_msg);
        lin_Search = (LinearLayout) findViewById(R.id.lin_Search);
        ed_search = (EditText) findViewById(R.id.ed_search);

    }

    private void setwallpaper() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        constraint_layout.setBackground(wallpaperDrawable);
    }

    private void listener() {

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible_applications(true);
            }
        });


        menu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.checkEditTime();

            }
        });

        menu_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"));
                startActivity(intent);
            }
        });
        menu_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowApp("com.android.camera");
            }
        });

        menu_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowApp("com.android.mms");
            }
        });

        menu_standby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.lockDevice();

            }
        });

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged",s.toString());
                    adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " + getTaskId());
        visible_applications(false);

    }


    private void visible_applications(boolean b) {
        if (b) {
            presenter.getAllApps();
            front_cover.setVisibility(View.GONE);
            lin_Search.setVisibility(View.VISIBLE);

//            gridview.setVisibility(View.VISIBLE);
        } else {
            presenter.getRecentApps();
            front_cover.setVisibility(View.VISIBLE);
            lin_Search.setVisibility(View.GONE);
//            gridview.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.splitter();
//        presenter.check_Service(this);
        presenter.check_receiver(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent: ");
    }

    @Override
    public void onBackPressed() {
        visible_applications(false);
    }

    @Override
    public void allowApp(String packageName) {
        Log.e(TAG, "allowApp: " + packageName);
        ed_search.setText("");
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(LaunchIntent);
    }

    @Override
    public void blockApp() {
        Log.e(TAG, "blockApp: ");
        Toast.makeText(this, "App is Blocked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void allowEdit() {
        Log.e(TAG, "allowEdit: ");
        startActivity(new Intent(LauncherActivity.this, MainActivity.class));
    }

    @Override
    public void setadapter(ArrayList<ItemDto> app_list) {
        adapter = new LauncherAdapter(this, app_list);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                presenter.isBlockTime(position);
            }
        });
    }

    @Override
    public ItemDto getAdapterItem(int position) {
        return adapter.getItem(position);
    }

    @Override
    public void enable_admin(ComponentName mDeviceAdmin) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "EXPLANATION");
        startActivityForResult(intent, 1);

    }

    @Override
    public void show_Dialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Setting message manually and performing action on button click
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), 0);
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Caution");
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "" + requestCode + " / " + resultCode + " / " + data);
        if (resultCode == 0) {
            presenter.checkIsAdminEnabled();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop_Receiver(this);
    }
}
