package com.rk.applock;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by user1 on 24/10/17.
 */
public class DeviceAdminSample extends DeviceAdminReceiver {

    void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "App_lock_Admin Enabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "App_lock Admin Disabled Request";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Admin Disabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
    }
}


