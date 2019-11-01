package com.rk.applock;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by user1 on 19/10/17.
 */
public class Package_info {
    String name;
    String package_name;
    Drawable icon;

    public Package_info(String name, String package_name, Drawable icon) {
        this.name = name;
        this.package_name = package_name;
        this.icon = icon;
    }
}
