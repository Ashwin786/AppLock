package com.rk.applock.launcher;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.rk.applock.R;

/**
 * Created by user1 on 3/1/19.
 */

public class Common_dialog extends Dialog implements View.OnClickListener {
    private final boolean cancel;
    private final int type;
    private final DialogCallback callback;
    Context dialogContext;
    Button okButton, cancelbutton;
    String message = "";
    private String btn_name;

    public Common_dialog(Context context, String msg, boolean cancel, DialogCallback callback, int type, String btn_name) {
        super(context);
        dialogContext = context;
        message = msg;
        this.callback = callback;
        this.cancel = cancel;
        this.type = type;
        this.btn_name = btn_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_common);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        setCancelable(false);
        Log.e("dialog inward..", "values..");

        okButton = (Button) findViewById(R.id.okButton);
        if (type == 2) {
            if (btn_name == null)
                btn_name = dialogContext.getString(R.string.confirm);
            okButton.setText(btn_name);
        }
        okButton.setOnClickListener(this);
        if (cancel) {
            cancelbutton = (Button) findViewById(R.id.cancelbutton);
            cancelbutton.setVisibility(View.VISIBLE);
            cancelbutton.setOnClickListener(this);
        }

        ((TextView) findViewById(R.id.alertTitleText)).setText(message);
    }



    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) dialogContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        switch (v.getId()) {
            case R.id.okButton:
                if (callback != null)
                    callback.onDialog_click(type);
                dismiss();
                break;
            case R.id.cancelbutton:
                callback.onDialog_click(0);
                dismiss();
                break;
            default:
                break;
        }

    }

}

