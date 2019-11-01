package com.rk.applock.launcher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.rk.applock.R;

import java.util.ArrayList;

/**
 * Created by user1 on 19/10/17.
 */
public class LauncherAdapter extends BaseAdapter {

    private final String TAG = "LauncherAdapter";
    private final LayoutInflater inflater;

    private final Context context;

    protected ArrayList<ItemDto> app_list, orginal_list;

    public LauncherAdapter(Context context, ArrayList<ItemDto> app_list) {
        this.context = context;
        this.app_list = new ArrayList<ItemDto>();
        this.app_list.addAll(app_list);
        this.orginal_list = app_list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        selected_list = new ArrayList<String>();
//        selected_list = Database.getInstance(context).get_apps_name();
//        Log.e("selected_list size ", Integer.toString(selected_list.size()));


    }


    @Override
    public int getCount() {
        return app_list.size();
    }

    @Override
    public ItemDto getItem(int i) {
        return app_list.get(i);
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
            view = inflater.inflate(R.layout.grid_single, null);
            holder.name = (TextView) view.findViewById(R.id.grid_text);
            holder.icon = (ImageView) view.findViewById(R.id.grid_image);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final ItemDto item = getItem(i);
        holder.name.setText(item.getApplicationLabel());
        holder.icon.setBackground(item.getApplicationIcon());

        return view;
    }


    class Holder {
        TextView name;
        ImageView icon;
    }


    private boolean isSystemPackage(PackageInfo pkgInfo) {

        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    public void filter(String text) {
        // TODO Auto-generated method stub
        app_list.clear();
        if (text.length() == 0) {
            app_list.addAll(orginal_list);
        } else {
            text = text.toLowerCase();
            for (ItemDto dto : orginal_list) {
                Log.e(TAG, "filter: " + dto.getApplicationLabel());
                if (dto.getApplicationLabel().toLowerCase().startsWith(text))
                    app_list.add(0, dto);
                else if (dto.getApplicationLabel().toLowerCase().contains(text)) {
                    app_list.add(dto);
                }
            }
        }
        notifyDataSetChanged();
    }
}
