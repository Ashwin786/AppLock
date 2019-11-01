package com.rk.applock.launcher;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class ItemDto implements Comparator<ItemDto> {


    String packageName;
    String applicationLabel;
    Drawable applicationIcon;

    public ItemDto(String applicationLabel, Drawable applicationIcon, String packageName) {
        this.applicationLabel = applicationLabel;
        this.applicationIcon = applicationIcon;
        this.packageName = packageName;
    }

    public ItemDto() {

    }

    public String getApplicationLabel() {
        return applicationLabel;
    }

    public Drawable getApplicationIcon() {
        return applicationIcon;
    }


    public String getPackageName() {
        return packageName;
    }


    @Override
    public int compare(ItemDto o1, ItemDto o2) {
        return o1.getApplicationLabel().compareTo(o2.getApplicationLabel());
    }
}
