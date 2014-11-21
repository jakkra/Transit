package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-19.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.adapters.SearchFragmentListAdapter;
import se.jakobkrantz.transit.app.apiasynctasks.SearchStationsTask;
import se.jakobkrantz.transit.app.database.DatabaseTransitSQLite;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;

import java.util.List;


/**
 * Class used when clicked on choose location to travel to/from
 */
public class SearchLocationFragment extends Fragment implements ListView.OnItemClickListener, TextWatcher {
    private ListView listView;
    private EditText searchView;
    private StationSelectedListener callBack;
    private SearchFragmentListAdapter searchFragmentListAdapter;
    private DatabaseTransitSQLite database;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface StationSelectedListener {

        /**
         * @param args MainFragment.SOURCE also includes saved state of MainFragment
         */
        public void onStationSelected(Bundle args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseTransitSQLite(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.search_location_fragment, container, false);

        List<Station> stations = database.getRecentStations(10);
        searchFragmentListAdapter = new SearchFragmentListAdapter(getActivity(), stations);
        listView = (ListView) view.findViewById(R.id.stations_list);
        searchView = (EditText) view.findViewById(R.id.search_station);
        searchView.addTextChangedListener(this);
        listView.setAdapter(searchFragmentListAdapter);
        listView.setOnItemClickListener(this);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.list_layout_controller);
        listView.setLayoutAnimation(controller);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        Bundle args = getArguments();
        String source = args.getString(MainFragment.SOURCE);
        Station s = ((Station) searchFragmentListAdapter.getItem(position));
        if (source.equals(MainFragment.SOURCE_FROM_STATION)) {

            args.putString(MainFragment.FROM_STATION, s.getStationName());
            args.putString(MainFragment.FROM_STATION_ID, Integer.toString(s.getStationId()));
            args.putString(MainFragment.FROM_STATION_LONG, Double.toString(s.getLongitude()));
            args.putString(MainFragment.FROM_STATION_LAT, Double.toString(s.getLatitude()));
            args.putString(MainFragment.FROM_STATION_TYPE, s.getType());
            args.putString(MainFragment.FROM_STATION_SEARCHED, s.getTimeSearched());

        } else if (source.equals(MainFragment.SOURCE_TO_STATION)) {

            args.putString(MainFragment.TO_STATION, s.getStationName());
            args.putString(MainFragment.TO_STATION_ID, Integer.toString(s.getStationId()));
            args.putString(MainFragment.TO_STATION_LONG, Double.toString(s.getLongitude()));
            args.putString(MainFragment.TO_STATION_LAT, Double.toString(s.getLatitude()));
            args.putString(MainFragment.TO_STATION_TYPE, s.getType());
            args.putString(MainFragment.TO_STATION_SEARCHED, s.getTimeSearched());
        }

        callBack.onStationSelected(args); //Changed to adpter.getPos(positon) will return string station

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            callBack = (StationSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StationSelectedListener");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 1) {
            SearchStationsTask searchTask = new SearchStationsTask(searchFragmentListAdapter);
            searchTask.execute(Constants.getSearchStationURL(s.toString()));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


}
