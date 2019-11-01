package com.rk.applock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user1 on 23/10/17.
 */
public class Start_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("App lock", "onReceive Start_receiver "+ System.currentTimeMillis());
        if (!isMyServiceRunning(context, MyService.class)) {
            context.startService(new Intent(context, MyService.class));
        }
        new Common().set_alarm(context, "et");
//        String outputPattern = "dd-MMM-yyyy hh:mm:ss";
//        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
//        String str = outputFormat.format(new Date());
//        Log.e("time", "" + str);

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
