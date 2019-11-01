package com.rk.applock.launcher;

import android.content.ComponentName;

import java.util.ArrayList;

public interface ViewNavigator {
    void allowApp(String packageName);

    void blockApp();

    void allowEdit();

    void setadapter(ArrayList<ItemDto> app_list);

    ItemDto getAdapterItem(int position);

    void enable_admin(ComponentName mDeviceAdmin);

    void show_Dialog(String message);
}
