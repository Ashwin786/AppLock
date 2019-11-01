package com.rk.applock.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ScreenON_OFF_Receiver extends BroadcastReceiver {

    private App_BlockPresenter appBlockPresenter;

    @Override
    public void onReceive(Context context, Intent intent) {
        appBlockPresenter = App_BlockPresenter.getInstance(context);
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e("Check", "Screen went OFF");
            appBlockPresenter.stopMonitor();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.e("Check", "Screen went ON");
            appBlockPresenter.startMonitor();
        }
    }
}
