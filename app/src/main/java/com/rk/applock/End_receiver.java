package com.rk.applock;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by user1 on 24/10/17.
 */
public class End_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("App lock", "onReceive End_receiver " + System.currentTimeMillis());
        if (isMyServiceRunning(context, MyService.class)) {
            context.stopService(new Intent(context, MyService.class));
        }
        new Common().set_alarm(context, "st");
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
