package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import se.jakobkrantz.transit.app.MainActivity;
import se.jakobkrantz.transit.app.R;

public class MainFragment extends Fragment implements View.OnClickListener {
    public final static String FROM_STATION = "fromstation";
    public final static String TO_STATION = "tostation";
    public final static String SOURCE = "source";
    public static final String SOURCE_TO_STATION = "sourcetostation";
    public static final String SOURCE_FROM_STATION = "sourcefromstation";

    TextView textView;
    TextView fromStation;
    TextView toStation;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
        toStation = (TextView) view.findViewById(R.id.text_view_to_station);
        fromStation = (TextView) view.findViewById(R.id.text_view_from_station);
        button = (Button) view.findViewById(R.id.button);
        fromStation.setOnClickListener(this);
        toStation.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fillStationsText(getArguments());
    }

    private void fillStationsText(Bundle args) {
        if (args != null) {
            String from = args.getString(FROM_STATION);
            String to = args.getString(TO_STATION);
            if (from != null) fromStation.setText(from);
            if (to != null) toStation.setText(to);
        }
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();

        if (v.getId() == R.id.text_view_from_station) args.putString(SOURCE, SOURCE_FROM_STATION);
        if (v.getId() == R.id.text_view_to_station) args.putString(SOURCE, SOURCE_TO_STATION);

        args.putString(MainFragment.FROM_STATION, fromStation.getText().toString());
        args.putString(MainFragment.TO_STATION, toStation.getText().toString());

        ((MainActivity) getActivity()).replaceFragment(MainActivity.FragmentTypes.SEARCH_STATION, args);

//        SearchStationsTask task = new SearchStationsTask(textView);
//        String station = stationInput.getText().toString().replace(" ", "%20");
//        task.execute("http://www.labs.skanetrafiken.se/v2.2/querystation.asp?inpPointfr=" +station);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            fillStationsText(savedInstanceState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current article selection in case we need to recreate the fragment
        outState.putString(MainFragment.TO_STATION, toStation.getText().toString());
        outState.putString(MainFragment.FROM_STATION, fromStation.getText().toString());

    }
}
