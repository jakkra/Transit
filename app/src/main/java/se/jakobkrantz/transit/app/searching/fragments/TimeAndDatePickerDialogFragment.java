package se.jakobkrantz.transit.app.searching.fragments;/*
 * Created by jakkra on 2015-02-04.
 */

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import se.jakobkrantz.transit.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeAndDatePickerDialogFragment extends DialogFragment implements View.OnClickListener {


    private String[] dates;
    private int[] year = new int[121];
    private NumberPicker datePicker;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private Button doneButton;
    private Button currentTimeButton;
    private Calendar rightNow = Calendar.getInstance();


    public TimeAndDatePickerDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dates = getDatesFromCalender();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_time_date_picker, container);
        datePicker = (NumberPicker) view.findViewById(R.id.datePicker);
        hourPicker = (NumberPicker) view.findViewById(R.id.hourPicker);
        minutePicker = (NumberPicker) view.findViewById(R.id.minutePicker);
        doneButton = (Button) view.findViewById(R.id.doneButton);
        currentTimeButton = (Button) view.findViewById(R.id.currentTimeButton);
        datePicker.setMinValue(0);
        datePicker.setMaxValue(dates.length - 1);
        datePicker.setFormatter(new NumberPicker.Formatter() {

            @Override
            public String format(int value) {
                return dates[value];
            }
        });
        datePicker.setDisplayedValues(dates);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(rightNow.get(Calendar.HOUR_OF_DAY));
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(rightNow.get(Calendar.MINUTE));
        datePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        doneButton.setOnClickListener(this);
        currentTimeButton.setOnClickListener(this);
        getDialog().setTitle(R.string.choose_time);
        return view;
    }

    private String[] getDatesFromCalender() {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        List<String> dates = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("EE, dd MMM", new Locale("sv", "SE"));
        dates.add(dateFormat.format(c1.getTime()));
        year[0] = c1.get(Calendar.YEAR);

        for (int i = 0; i < 60; i++) {
            c1.add(Calendar.DATE, 1);
            year[i + 1] = c1.get(Calendar.YEAR);

            dates.add(dateFormat.format(c1.getTime()));
        }

        c2.add(Calendar.DATE, -61);
        for (int i = 0; i < 60; i++) {
            c2.add(Calendar.DATE, 1);
            year[61 + i] = c1.get(Calendar.YEAR);
            dates.add(dateFormat.format(c2.getTime()));
        }
        return dates.toArray(new String[dates.size() - 1]);
    }

    @Override
    public void onClick(View v) {
        OnTimeSetListener callback;
        try {
            callback = (OnTimeSetListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(this.getClass().getSimpleName(), "OnTimeSetListener of this class must be implemented by target fragment!", e);
            throw e;
        }
        if (callback != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MMM/dd HH:mm");

            if (v.getId() == R.id.doneButton) {
                sdf.setLenient(false);
                String[] d = dates[datePicker.getValue()].split(" ");
                String date = year[datePicker.getValue()] + "/" + d[2] + "/" + d[1] + " " + hourPicker.getValue() + ":" + minutePicker.getValue();
                Date dt2;
                try {
                    dt2 = sdf.parse(date);
                    callback.onTimeSet(dt2);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } else if (v.getId() == R.id.currentTimeButton) {
                Log.d("Callback Time", rightNow.getTime().toString());
                callback.onTimeSet(rightNow.getTime());


            }

        }
        getDialog().dismiss(); //TODO Not recommended to do this, should be changed.
    }


    public interface OnTimeSetListener {
        public void onTimeSet(Date date);
    }
}
