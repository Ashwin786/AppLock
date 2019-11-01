package com.rk.applock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected long end_time;
    private final String TAG = "MainActivity";
    private ListView listview;
    public TextView tv_selected;
    private Button btn_done;
    ComponentName mDeviceAdmin;
    DevicePolicyManager mDPM;
    private Button btn_admin,btn_hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        int HOUR = Calendar.getInstance().get(Calendar.HOUR);
//        int HOUR_OF_DAY = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        Log.e("hours",HOUR+" / "+HOUR_OF_DAY);
//        if(1==1)
//            return;
//        hide_app();
        /*if (isMyServiceRunning(this, MyService.class)) {
            stopService(new Intent(this, MyService.class));
        }*/
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(MainActivity.this, DeviceAdminSample.class);
        btn_admin = (Button) findViewById(R.id.btn_admin);
        btn_hide = (Button) findViewById(R.id.btn_hide);
        if (!mDPM.isAdminActive(mDeviceAdmin))
            btn_admin.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDPM.isAdminActive(mDeviceAdmin))
                    enable_admin(mDeviceAdmin);
                else {
                    mDPM.removeActiveAdmin(mDeviceAdmin);
                    btn_admin.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
//                    Toast.makeText(MainActivity.this, "Already your admin", Toast.LENGTH_SHORT);
                }

            }
        });

        if (!check_Is_usage_given())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        listview = (ListView) findViewById(R.id.listview);
        tv_selected = (TextView) findViewById(R.id.tv_selected);
        btn_done = (Button) findViewById(R.id.btn_done);
        ((Button) findViewById(R.id.btn_hide)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                hide_unhide_app();
                mDPM.lockNow();
            }
        });
        final CustomAdapter adapter = new CustomAdapter(this);
        listview.setAdapter(adapter);
        tv_selected.setText(" Selected ( " + Integer.toString(adapter.selected_list.size()) + " )");
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.selected_list.size() > 0) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    sp.edit().clear().commit();
                    Database.getInstance(MainActivity.this).delete_data();
                    Database.getInstance(MainActivity.this).insert_data(adapter.selected_list);
                    get_time_picker("Start time");
//                    startService(new Intent(MainActivity.this, MyService.class));
//                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Please select atleast one app", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        hide_app();

    }

    private boolean check_Is_usage_given() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void enable_admin(ComponentName mDeviceAdmin) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "EXPLANATION");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "" + requestCode + " / " + resultCode + " / " + data);
        if (resultCode == 0) {
            Toast.makeText(MainActivity.this, "Kindly click Activate Device Admin option", Toast.LENGTH_SHORT).show();
            finish();
        } else {
//            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            btn_admin.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
        }
    }

    protected void get_time_picker(String title) {
        TimePickerFragment dialog = new TimePickerFragment();
        dialog.show(getFragmentManager(), title);
    }

    private boolean check_permission() {
        String permission = "android.permission.PACKAGE_USAGE_STATS";
//        return getPackageManager().checkPermission(permission,getPackageName())== PackageManager.PERMISSION_GRANTED;
        return checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
//        new Common().set_alarm(this, "st");
    }

    private List<ApplicationInfo> get_installed_app() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//        for (ApplicationInfo packageInfo : packages) {
//            Package_info pack = new Package_info(packageInfo.name,packageInfo.packageName,packageInfo.loadIcon(pm));
//            Log.e(TAG, "Installed app name :" + pm.getApplicationLabel(packageInfo));
//            Log.e(TAG, "Installed package :" + packageInfo.packageName);
//            Log.e(TAG, "Source dir : " + packageInfo.sourceDir);
//            Log.e(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
//        }
        return packages;
    }

    private void hide_unhide_app() {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        int what = p.getComponentEnabledSetting(componentName);
        if (what == 2) {
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            btn_hide.setText("Hide");
        }else {
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            btn_hide.setText("un hide");
        }

        Log.e("componentName ", "" + what);

    }

    private void un_hide_app() {
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    protected void start_alarm(long start_time) {
//        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(MainActivity.this, Start_receiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, start_time, 86400000, pintent);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, start_time, 86400000, pintent);
//        get_time_picker("End Time");
//        finish();
    }

    protected void end_alarm(long end_time) {
        Intent intent = new Intent(MainActivity.this, End_receiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, end_time, 86400000, pintent);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, end_time, 30000, pintent);
//        hide_app();
//        new Repeat_time_dialog(this).show();

    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
