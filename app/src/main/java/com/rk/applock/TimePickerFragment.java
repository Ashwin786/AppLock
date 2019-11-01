package com.rk.applock;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by user1 on 23/10/17.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        //Create and return a new instance of TimePickerDialog
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        dialog.setTitle(getTag());
        return dialog;
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        Log.e("getTag",getTag());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hourOfDay, minute);
        Log.e("getmillis",""+cal.getTimeInMillis());
        if (getTag().equals("Start time")) {
            sp.edit().putString("st", hourOfDay+":"+minute).commit();
            ((MainActivity) getActivity()).get_time_picker("End Time");
//            ((MainActivity) getActivity()).start_alarm(cal.getTimeInMillis());
        } else {
//            ((MainActivity) getActivity()).end_alarm(cal.getTimeInMillis());
            sp.edit().putString("et", hourOfDay+":"+minute).commit();
//            new Repeat_time_dialog(getActivity()).show();
            sp.edit().putLong("repeat_sec",500).commit();
        }

//        Log.e("time in second", "" + date.getTime());
//        String outputPattern = "dd-MMM-yyyy hh:mm:ss";
//        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
//        String str = outputFormat.format(date);
//        Log.e("time", "" + str);

    }

    public Date parseDate(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
//        String outputPattern = "dd-MMM-yyyy h:mm a";
//        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
//            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}
