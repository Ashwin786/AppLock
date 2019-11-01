package com.rk.applock.launcher;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.rk.applock.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class App_BlockPresenter {
    private static Timer timer;
    private static App_BlockPresenter ourInstance;
    private static Context context;
    private PowerManager pm;
    private ArrayList<String> blockAppList = new ArrayList<>();
    private static final String TAG = " App_BlockPresenter";

    public static App_BlockPresenter getInstance(Context mcontext) {
        context = mcontext;
        if (ourInstance == null)
            ourInstance = new App_BlockPresenter();

        return ourInstance;
    }

    private App_BlockPresenter() {
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        blockAppList.add("com.miui.home");
        blockAppList.add("com.miui.securitycenter");
        blockAppList.add("com.android.systemui");
        blockAppList.addAll(Database.getInstance(context).get_apps_name());
    }

    public void stopMonitor() {
        Log.e(TAG, "checkAppBlock: stopMonitor");
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void startMonitor() {
        Log.e(TAG, "checkAppBlock: startMonitor");

        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(timerTask(), 100, 500);
        }

    }

    private TimerTask timerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (pm.isScreenOn())
                    toastHandler.sendEmptyMessage(0);
            }

        };
    }

    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            checkAppBlock();
        }
    };

    private void checkAppBlock() {
//        Log.e(TAG, "checkAppBlock: Running");
//        Log.e(TAG, "checkAppBlock: timer.purge after schedule" + timer.purge());

        String currentApp = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(
                            mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();

        }
//        Log.e(TAG, "currentApp: " + currentApp);

        if (!currentApp.equals("com.rk.applock")) {
            for (int i = 0; i < blockAppList.size(); i++) {
                if (currentApp.equals(blockAppList.get(i))) {
                    /*Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(startMain);*/
                    context.startActivity(new Intent(context,LauncherActivity.class));
                    break;
                }

            }
        }
    }
}
