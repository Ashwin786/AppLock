package com.rk.applock;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 19/10/17.
 */
public class CustomAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final PackageManager pm;
    private final Context context;
    private List<PackageInfo> packages = null, temp_packages = null;
    protected ArrayList<String> selected_list;

    public CustomAdapter(Context context) {
        this.context = context;
//        this.packages = packages;
        pm = context.getPackageManager();
//        this.packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        this.packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        remove_unwanted_apps();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selected_list = new ArrayList<String>();
        selected_list = Database.getInstance(context).get_apps_name();
        Log.e("selected_list size ", Integer.toString(selected_list.size()));
    }

    private void remove_unwanted_apps() {
        for (int i = packages.size() - 1; i >= 0; i--) {
            if (is_installedApp(packages.get(i)) && !packages.get(i).packageName.equals("com.android.settings"))
                packages.remove(i);
        }
    }

    private boolean is_installedApp(PackageInfo packageInfo) {
        Intent intent = pm
                .getLaunchIntentForPackage(packageInfo.packageName);

        if (intent == null)
            return true;
        else
            return false;
    }

    @Override
    public int getCount() {
        return packages.size();
    }

    @Override
    public PackageInfo getItem(int i) {
        return packages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.custom_view, null);
            holder.name = (TextView) view.findViewById(R.id.tv);
            holder.icon = (ImageView) view.findViewById(R.id.iv);
            holder.cb = (CheckBox) view.findViewById(R.id.cb);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final PackageInfo item = getItem(i);
        holder.name.setText(pm.getApplicationLabel(item.applicationInfo));
        holder.icon.setBackground(pm.getApplicationIcon(item.applicationInfo));
        if (selected_list.contains(item.packageName))
            holder.cb.setChecked(true);
        else
            holder.cb.setChecked(false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_list.contains(item.packageName)) {
                    holder.cb.setChecked(false);
                    selected_list.remove(item.packageName);
                } else {
                    holder.cb.setChecked(true);
                    selected_list.add(item.packageName);
                }
                updateUI();
//                Intent intent = pm.getLaunchIntentForPackage(item.packageName);
//                context.startActivity(intent);
            }
        });
        /*holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    Log.e("checked status","true");
                else
                    Log.e("checked status","false");
                *//*if (selected_list.contains(item.packageName))
                    selected_list.remove(item.packageName);
                else
                    selected_list.add(item.packageName);*//*
            }
        });*/
        return view;
    }

    private void updateUI() {

        ((MainActivity) context).tv_selected.setText(" Selected ( " + Integer.toString(selected_list.size()) + " )");
    }

    class Holder {
        TextView name;
        ImageView icon;
        CheckBox cb;
    }


    private boolean isSystemPackage(PackageInfo pkgInfo) {

            return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true: false;
    }
}
