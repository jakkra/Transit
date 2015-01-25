package se.jakobkrantz.transit.app.disturbances.fragments;/*
 * Created by jakkra on 2015-01-25.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.markushi.ui.CircleButton;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.utils.GcmConstants;

public class DisturbancesFragment extends Fragment {
    private TextView textView;
    private CircleButton reportButton;
    private Bundle data;
    private View.OnClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disturbances, container, false);
        textView = (TextView) view.findViewById(R.id.disturbance_info);
        reportButton = (CircleButton) view.findViewById(R.id.disturbance_button);
        reportButton.setOnClickListener(listener);
        data = getArguments();
        Log.d("OnCreateView", "getArguments()");

        if (data != null) {
            fillData(data);
        } else {
            if (savedInstanceState != null)
                fillData(savedInstanceState);
        }
        return view;
    }


    private void fillData(Bundle data) {
        String from = data.getString(GcmConstants.DISTURBANCE_FROM_STATION_NAME);
        String to = data.getString(GcmConstants.DISTURBANCE_TO_STATION_NAME);
        String type = data.getString(GcmConstants.DISTURBANCE_TYPE);
        String delay = data.getString(GcmConstants.DISTURBANCE_APPROX_MINS);
        String note = data.getString(GcmConstants.DISTURBANCE_NOTE);

        String info = "Försening mellan " + from + " och " + to + "\n"
                + "Typ: " + type + "\n"
                + "Approximerad försening: " + delay + "\n"
                + "Notering: " + note;
        Log.d("fillData", info);
        textView.setText(info);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Dist", "Saving");
        if (outState != null && data != null) {
            outState.putAll(data);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d("Activity create", savedInstanceState.toString());

            // Restore last state for checked position.
            //fillData(savedInstanceState);
        } else {
            if (data != null) {
                // fillData(data);
            } else {
                //textView.setText("Inga rapporteringar");

            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (View.OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnClickListener");
        }
    }

}
