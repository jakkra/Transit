package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import se.jakobkrantz.transit.app.MainActivity;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.apiasynctasks.SearchJourneysTask;
import se.jakobkrantz.transit.app.database.DatabaseTransitSQLite;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    public static final String SOURCE = "source";
    public static final String SOURCE_TO_STATION = "sourcetostation";
    public static final String SOURCE_FROM_STATION = "sourcefromstation";
    public static final String FROM_STATION = "fromstation";
    public static final String FROM_STATION_ID = "fromstatid";
    public static final String FROM_STATION_LONG = "fromstatlong";
    public static final String FROM_STATION_LAT = "fromstatlat";
    public static final String FROM_STATION_TYPE = "fromstattype";
    public static final String FROM_STATION_SEARCHED = "fromstatsearchedtime";
    public static final String TO_STATION = "tostation";
    public static final String TO_STATION_ID = "tostattid";
    public static final String TO_STATION_LONG = "tostatlong";
    public static final String TO_STATION_LAT = "tostatlat";
    public static final String TO_STATION_TYPE = "tostattype";
    public static final String TO_STATION_SEARCHED = "tostatsearch";

    private TextView textView;
    private TextView fromStation;
    private TextView toStation;
    private Button button;
    private DatabaseTransitSQLite database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseTransitSQLite(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
        toStation = (TextView) view.findViewById(R.id.text_view_to_station);
        fromStation = (TextView) view.findViewById(R.id.text_view_from_station);
        button = (Button) view.findViewById(R.id.button);
        button.setOnTouchListener(this);
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
        Bundle args = getArguments();
        if (args == null) args = new Bundle();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            SearchJourneysTask task = new SearchJourneysTask(textView);
            Bundle b = getArguments();
            Log.d("Bundle nbr", b.getString(FROM_STATION_ID) + "");
            List<Station> recentSearches = new ArrayList<Station>();

            recentSearches.add(new Station(b.getString(FROM_STATION), Integer.parseInt(b.getString(FROM_STATION_ID)), Double.parseDouble(b.getString(FROM_STATION_LAT)), Double.parseDouble(b.getString(FROM_STATION_LONG)), b.getString(FROM_STATION_TYPE)));
            recentSearches.add(new Station(b.getString(TO_STATION), Integer.parseInt(b.getString(TO_STATION_ID)), Double.parseDouble(b.getString(TO_STATION_LAT)), Double.parseDouble(b.getString(TO_STATION_LONG)), b.getString(TO_STATION_TYPE)));

            database.addStationsToRecent(recentSearches);
            task.execute(Constants.getURL(b.getString(FROM_STATION_ID), b.getString(TO_STATION_ID), Constants.getCurrentDate(), Constants.getCurrentTime(), 5));
            return true;
        }
        return false;
    }
}
