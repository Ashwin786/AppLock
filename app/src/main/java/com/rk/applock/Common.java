package com.rk.applock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by user1 on 21/11/17.
 */
public class Common {

    public void set_alarm(Context context, String title) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String st = sp.getString(title, "");
        if (!st.isEmpty()) {
            if (!isMyServiceRunning(context, MyService.class)) {
                context.startService(new Intent(context, MyService.class));
            }

           /* Intent intent_start_alarm;
            if (title.equals("st"))
                intent_start_alarm = new Intent(context, Start_receiver.class);
            else
                intent_start_alarm = new Intent(context, End_receiver.class);
            PendingIntent pintent_start = PendingIntent.getBroadcast(context, 0, intent_start_alarm, 0);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            alarm.setRepeating(AlarmManager.RTC_WAKEUP, split(st), 3600000, pintent_start);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, split(st), 86400000, pintent_start);*/
        }

    }

    private void end_alarm() {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        AlarmManager alarm2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        String et = sp.getString("et", "");
//        if (!et.isEmpty()) {
//            Intent intent_end_alarm = new Intent(context, End_receiver.class);
//            PendingIntent pintent_end = PendingIntent.getBroadcast(context, 0, intent_end_alarm, 0);
//            alarm2.setRepeating(AlarmManager.RTC_WAKEUP, split(et), 86400000, pintent_end);
//        }
    }

    private long split(String st) {
        Calendar cal = Calendar.getInstance();
        String[] splitter = st.split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitter[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(splitter[1]));
        long time = cal.getTimeInMillis();
        Log.e(st + "time", "" + time);
        return time;
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
