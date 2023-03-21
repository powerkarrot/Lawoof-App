package com.example.karrot.lawoof.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI;
import com.example.karrot.lawoof.Util.CallableActivity.CallbackTask;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

/**
 * Created by karrot on 11.11.2016.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener, CallbackTask {

    private CallbackUI activity;
    private String result;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        this.result = hourOfDay + ":" + minute;
        call();
    }
    public void setCallback(CallbackUI activity){
        this.activity = activity;
    }

    @Override
    public void call() {
        this.activity.updateUI("time", result);
    }
}
