package com.example.karrot.lawoof.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.karrot.lawoof.Util.CallableActivity.CallbackUI;
import com.example.karrot.lawoof.Util.CallableActivity.CallbackTask;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

/**
 * Created by karrot on 11.11.2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, CallbackTask {

    private CallbackUI activity;
    private String result;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        month++;
        result = day + "." + month + "." + year;
        call();
    }

    public void setCallback(CallbackUI activity){
        this.activity = activity;
    }

    @Override
    public void call() {
        activity.updateUI("date" ,result);
    }
}
