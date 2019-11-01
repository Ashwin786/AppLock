package com.rk.applock;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by user1 on 1/11/17.
 */
public class Repeat_time_dialog extends Dialog{

    private final Context context;
    private EditText ed_ms;

    public Repeat_time_dialog(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeat_dialog);
        ed_ms=(EditText)findViewById(R.id.ed_ms);
        ((Button)findViewById(R.id.ok_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ed_ms.getText().toString().trim();
                if (text.length()<3) {
                    Toast.makeText(context, "Enter text", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                    sp.edit().putLong("repeat_sec",Long.parseLong(text)).commit();
                    dismiss();
//                    ((MainActivity)context).finish();
                }
            }
        });

    }
}
