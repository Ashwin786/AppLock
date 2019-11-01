package com.rk.applock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Boot_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("boot receiver","boot receiver");
        String st = PreferenceManager.getDefaultSharedPreferences(context).getString("st", "");
        if (!st.isEmpty()) {
//            new Common().set_alarm(context, "st");
            long current_time = Calendar.getInstance().getTimeInMillis();
            if (current_time > split(st)) {
                if (!isMyServiceRunning(context, MyService.class))
                    context.startService(new Intent(context, MyService.class));
            } else if (isMyServiceRunning(context, MyService.class))
                context.stopService(new Intent(context, MyService.class));
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

    private long split(String st) {
        Calendar cal = Calendar.getInstance();
        String[] splitter = st.split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitter[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(splitter[1]));
        long time = cal.getTimeInMillis();
        return time;
    }
}
