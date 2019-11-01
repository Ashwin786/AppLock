package com.rk.applock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.rk.applock.launcher.App_BlockPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

/**
 * Created by user1 on 19/10/17.
 */
public class MyService extends Service {

    private static final String TAG = " App lock MyService";
    private App_BlockPresenter appBlockPresenter;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
        appBlockPresenter = App_BlockPresenter.getInstance(this);
        Log.e("App lock", "Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appBlockPresenter.startMonitor();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("App lock", "Service stopped");
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
        appBlockPresenter.stopMonitor();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, "onTaskRemoved");
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), MyService.class),
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }
}

