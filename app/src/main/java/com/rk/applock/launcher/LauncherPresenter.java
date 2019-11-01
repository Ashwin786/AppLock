package com.rk.applock.launcher;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.rk.applock.Database;
import com.rk.applock.DeviceAdminReceiver;
import com.rk.applock.DeviceAdminSample;
import com.rk.applock.MyService;
import com.rk.applock.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class LauncherPresenter {
    private static final String TAG = "LauncherPresenter";
    private final ViewNavigator viewNavigator;
    private final Context context;
    private int startTime = 18;
    private int endTime = 2;
    private List<PackageInfo> packages = null;
    protected ArrayList<ItemDto> app_list;
    HashSet<ItemDto> recentList;
    private PackageManager pm = null;
    private DevicePolicyManager mDPM;
    private ScreenON_OFF_Receiver screenReceiver = new ScreenON_OFF_Receiver();
    private boolean screenReceiver_register = false;

    public DevicePolicyManager mDevicePolicyManager;
    public PackageManager mPackageManager;

    public ComponentName mAdminComponentName;
    private final String[] APP_PACKAGES = {};
    public LauncherPresenter(Context context, ViewNavigator viewNavigator) {
        this.context = context;
        this.viewNavigator = viewNavigator;
        this.pm = context.getPackageManager();
        this.packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        app_list = new ArrayList<>();
        recentList = new HashSet<>();
    }

    public void splitter() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String[] split = sp.getString("st", "00:00").split(":");
        this.startTime = Integer.parseInt(split[0]);
        split = sp.getString("et", "00:00").split(":");
        this.endTime = Integer.parseInt(split[0]);
    }

    public void isBlockTime(int position) {
        ItemDto dto = viewNavigator.getAdapterItem(position);
        String packageName = dto.getPackageName();
        if (isBlockTime() && checkBlockAppList(packageName))
            viewNavigator.blockApp();
        else {
            recentList.add(dto);
            viewNavigator.allowApp(packageName);
        }


    }

    private boolean checkBlockAppList(String currentApp) {
        ArrayList<String> packageName_list = Database.getInstance(context).get_apps_name();
        for (int i = 0; i < packageName_list.size(); i++) {
            if (currentApp.equals(packageName_list.get(i)) || currentApp.equals("com.miui.securitycenter")) {
                return true;
            }
        }
        return false;
    }

    private int getCurrentHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public boolean isBlockTime() {
        if (!isAutomatic_time_Enabled())
            return true;
        if (startTime != 0 && endTime != 0) {
            if (startTime <= endTime) {
                if (getCurrentHour() >= startTime && getCurrentHour() <= endTime)
                    return true;
            } else {
                if (getCurrentHour() >= startTime || getCurrentHour() <= endTime)
                    return true;
            }
        }
        return false;
    }

    public void check_Service(Context context) {
        if (isMyServiceRunning(context, MyService.class)) {
            // service running
            if (!isBlockTime())
                context.stopService(new Intent(context, MyService.class));
        } else {
            // service not running
            if (isBlockTime())
                context.startService(new Intent(context, MyService.class));
        }
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

    public void checkEditTime() {
        if (!isBlockTime())
            viewNavigator.allowEdit();
    }

    protected void setAllAppList() {
        for (int i = packages.size() - 1; i >= 0; i--) {
            if (is_installedApp(packages.get(i)) && !packages.get(i).packageName.equals("com.android.settings"))
                packages.remove(i);
            else
                app_list.add(new ItemDto((String) pm.getApplicationLabel(packages.get(i).applicationInfo), pm.getApplicationIcon(packages.get(i).applicationInfo), packages.get(i).packageName));
        }
        Collections.sort(app_list, new ItemDto());
    }

    private boolean is_installedApp(PackageInfo packageInfo) {
        if (packageInfo.packageName.equals("com.rk.applock"))
            return true;
        else if (pm.getLaunchIntentForPackage(packageInfo.packageName) == null)
            return true;
        else
            return false;
    }

    public void getRecentApps() {
        viewNavigator.setadapter(new ArrayList<ItemDto>(recentList));
    }

    public void getAllApps() {
        viewNavigator.setadapter(app_list);
    }

    public void checkIsAdminEnabled() {
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdmin = new ComponentName(context, DeviceAdminSample.class);
        if (!mDPM.isAdminActive(mDeviceAdmin))
            viewNavigator.enable_admin(mDeviceAdmin);
    }

    public void lockDevice() {
        if (mDPM != null)
            mDPM.lockNow();
    }

    private boolean isAutomatic_time_Enabled() {
        int auto_time = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0);
        Log.e("AUTO_TIME status", "" + auto_time);
        int auto_time_zone = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);

        if (auto_time == 0) {
            viewNavigator.show_Dialog(context.getString(R.string.incorrect_time));
//            new Common_dialog(context, context.getString(R.string.incorrect_time), false, this, 2, context.getString(R.string.ok)).show();
            return false;
        } else if (auto_time_zone == 0) {
            viewNavigator.show_Dialog(context.getString(R.string.incorrect_zone));
//            new Common_dialog(context, context.getString(R.string.incorrect_zone), false, this, 2, context.getString(R.string.ok)).show();
            return false;
        }
        return true;
    }


    public void start_Receiver(Context context) {

            IntentFilter screenStateFilter = new IntentFilter();
            screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
            screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(screenReceiver, screenStateFilter);
            screenReceiver_register = true;
    }

    protected void check_receiver(Context context) {
        if(screenReceiver_register){
//            Receiver is running
            if (!isBlockTime()) {
                stop_Receiver(context);
                App_BlockPresenter.getInstance(context).stopMonitor();
            }
        }else {
            // Receiver not running
            if (isBlockTime()) {
                App_BlockPresenter.getInstance(context).startMonitor();
                start_Receiver(context);
            }
        }
    }

    public void stop_Receiver(Context context) {
        if (screenReceiver_register) {
            context.unregisterReceiver(screenReceiver);
        }
        screenReceiver_register = false;
    }

    protected void cosuLock() {
        mDevicePolicyManager = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPackageManager = context.getPackageManager();
        if (mDevicePolicyManager.isDeviceOwnerApp(
                context.getApplicationContext().getPackageName())) {
            mPackageManager.setComponentEnabledSetting(
                    new ComponentName(context.getApplicationContext(),
                            LauncherActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            Toast.makeText(context.getApplicationContext(),
                    "This app has not been given Device Owner\n" +
                            "        privileges to manage this device and start lock task mode", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected void set_device_admin() {
        mDevicePolicyManager = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = DeviceAdminReceiver.getComponentName(context);
        mDevicePolicyManager = (DevicePolicyManager)
                context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mPackageManager = context.getPackageManager();
        // Check to see if started by LockActivity and disable LockActivity if so


        if (mDevicePolicyManager.isDeviceOwnerApp(context.getPackageName())) {
            setDefaultCosuPolicies(true);
        } else {
            Toast.makeText(context.getApplicationContext(),
                    "This app is not set as device owner\n" +
                            "        and cannot start lock task mode", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setDefaultCosuPolicies(boolean active) {
        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        // disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // set system update policy
        if (active) {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    null);
        }

        // set this Activity as a lock task package

      /*  mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? new String[]{getPackageName()} : new String[]{});*/

        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? APP_PACKAGES : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            context.getPackageName(), LauncherActivity.class.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, context.getPackageName());
        }
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,
                    restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction);
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled) {
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                            | BatteryManager.BATTERY_PLUGGED_USB
                            | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        } else {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    "0"
            );
        }
    }

}
