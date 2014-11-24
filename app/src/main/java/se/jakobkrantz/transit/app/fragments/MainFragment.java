package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import se.jakobkrantz.transit.app.MainActivity;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.adapters.FavouriteListAdapter;
import se.jakobkrantz.transit.app.apiasynctasks.SearchJourneysTask;
import se.jakobkrantz.transit.app.database.DatabaseTransitSQLite;
import se.jakobkrantz.transit.app.database.SimpleJourney;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;
import se.jakobkrantz.transit.app.skanetrafikenAPI.TimeAndDateConverter;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener, FavouriteListAdapter.OnItemChangeListener, SearchJourneysTask.DataDownloadListener {
    //TODO Change to enum
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
    public static final int NBR_OF_LIST_ITEM_TO_SHOW = 10;

    private TextView fromStation;
    private TextView toStation;
    private Button searchButton;
    private Button favButton;
    private DatabaseTransitSQLite database;
    private RecyclerView listView;
    private FavouriteListAdapter favListAdapter;
    private ProgressBar progressBar;
    private TextView dep;
    private TextView arr;
    private TextView transportType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        database = new DatabaseTransitSQLite(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        toStation = (TextView) view.findViewById(R.id.text_view_to_station);
        fromStation = (TextView) view.findViewById(R.id.text_view_from_station);
        searchButton = (Button) view.findViewById(R.id.button);
        favButton = (Button) view.findViewById(R.id.favourite_button);
        listView = (RecyclerView) view.findViewById(R.id.listView);
        arr = (TextView) view.findViewById(R.id.arr_time);
        dep = (TextView) view.findViewById(R.id.dep_time);
        transportType = (TextView) view.findViewById(R.id.transport_type);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        favListAdapter = new FavouriteListAdapter(database.getFavouriteJourneys(NBR_OF_LIST_ITEM_TO_SHOW), database.getRecentJourneys(NBR_OF_LIST_ITEM_TO_SHOW), this, NBR_OF_LIST_ITEM_TO_SHOW);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(mLayoutManager);
        listView.setHasFixedSize(true);
        listView.setAdapter(favListAdapter);
        listView.setItemAnimator(new DefaultItemAnimator());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fromStation.setOnClickListener(this);
        toStation.setOnClickListener(this);

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = getArguments();
                Station s1 = new Station(b.getString(FROM_STATION), Integer.parseInt(b.getString(FROM_STATION_ID)), Double.parseDouble(b.getString(FROM_STATION_LAT)), Double.parseDouble(b.getString(FROM_STATION_LONG)), b.getString(FROM_STATION_TYPE));
                Station s2 = new Station(b.getString(TO_STATION), Integer.parseInt(b.getString(TO_STATION_ID)), Double.parseDouble(b.getString(TO_STATION_LAT)), Double.parseDouble(b.getString(TO_STATION_LONG)), b.getString(TO_STATION_TYPE));
                SimpleJourney s = new SimpleJourney(s1, s2);
                if (database.addStationFavPair(s)) {
                    favListAdapter.addFavourite(s);
                }


            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = getArguments();
                if(b == null){
                    b = new Bundle();
                }
                List<Station> recentSearches = new ArrayList<Station>();
                SimpleJourney s;
                s = database.getSimpleJourneyFromRecentOrFavs(fromStation.getText().toString(), toStation.getText().toString());

                if (s == null) {
                    recentSearches.add(new Station(b.getString(FROM_STATION), Integer.parseInt(b.getString(FROM_STATION_ID)), Double.parseDouble(b.getString(FROM_STATION_LAT)), Double.parseDouble(b.getString(FROM_STATION_LONG)), b.getString(FROM_STATION_TYPE)));
                    recentSearches.add(new Station(b.getString(TO_STATION), Integer.parseInt(b.getString(TO_STATION_ID)), Double.parseDouble(b.getString(TO_STATION_LAT)), Double.parseDouble(b.getString(TO_STATION_LONG)), b.getString(TO_STATION_TYPE)));
                    s = new SimpleJourney(recentSearches.get(0), recentSearches.get(1));
                    Log.d("Not in database using args to get ", s.getFromStation() + " to" + s.getToStation());
                } else {
                    b = new Bundle();
                    Station s1 = s.getFromStation();
                    Station s2 = s.getToStation();
                    b.putString(MainFragment.FROM_STATION, s1.getStationName());
                    b.putString(MainFragment.FROM_STATION_ID, Integer.toString(s1.getStationId()));
                    b.putString(MainFragment.FROM_STATION_LONG, Double.toString(s1.getLongitude()));
                    b.putString(MainFragment.FROM_STATION_LAT, Double.toString(s1.getLatitude()));
                    b.putString(MainFragment.FROM_STATION_TYPE, s1.getType());
                    b.putString(MainFragment.FROM_STATION_SEARCHED, s1.getTimeSearched());

                    b.putString(MainFragment.TO_STATION, s2.getStationName());
                    b.putString(MainFragment.TO_STATION_ID, Integer.toString(s2.getStationId()));
                    b.putString(MainFragment.TO_STATION_LONG, Double.toString(s2.getLongitude()));
                    b.putString(MainFragment.TO_STATION_LAT, Double.toString(s2.getLatitude()));
                    b.putString(MainFragment.TO_STATION_TYPE, s2.getType());
                    b.putString(MainFragment.TO_STATION_SEARCHED, s2.getTimeSearched());
                }


                database.addRecentJourneySearch(s);
                database.addStationsToRecent(recentSearches);
                favListAdapter.addRecentJourney(s);
                ((MainActivity) getActivity()).replaceFragment(MainActivity.FragmentTypes.SEARCH_RESULT, b);


            }
        });
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Do Activity menu item stuff here
                return true;
            case R.id.action_clear_recent_searches:
                database.clearRecentJourneySearches();
                favListAdapter.clearAllSearches();
                return true;
            case R.id.action_clear_favourites:
                database.clearAllFavourites();
                favListAdapter.clearAllFavourites();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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
    public void onRecentSearchItemAdded(SimpleJourney s) {
        database.deleteRecentJourney(s);
    }

    @Override
    public void onItemClickListener(SimpleJourney s) {
        SearchJourneysTask task = new SearchJourneysTask();
        task.setDataDownloadListener(this);
        String url = Constants.getURL(s.getFromStation().getStationId(), s.getToStation().getStationId(), Constants.getCurrentDate(), Constants.getCurrentTime(), 1);
        progressBar.setVisibility(View.VISIBLE);
        task.execute(url);
        fromStation.setText(s.getFromStation().toString());
        toStation.setText(s.getToStation().toString());
    }

    @Override
    public void onItemLongClickListener(SimpleJourney s) {
        Toast.makeText(getActivity(), s.getFromStation() + " -> " + s.getToStation(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFavouriteItemAdded(SimpleJourney s) {
        //List was full, more than 10? favourites, deletes old
        Toast.makeText(getActivity(), "Favourite journey limit reached, oldest was removed", Toast.LENGTH_LONG).show();
        database.deleteFavouriteJourney(s);
    }

    @Override
    public void onFavouriteItemRemoved(SimpleJourney s) {
        database.deleteFavouriteJourney(s);
    }

    @Override
    public void dataDownloadedSuccessfully(Object data) {
        progressBar.setVisibility(View.GONE);
        List<Journey> journeyList = (List<Journey>) data;
        Journey j = journeyList.get(0);
        dep.setText(TimeAndDateConverter.formatTime(j.getDepDateTime()));
        arr.setText(TimeAndDateConverter.formatTime(j.getArrDateTime()));
        transportType.setText(j.getRouteLinks().get(0).getTransportModeName() + " " + j.getRouteLinks().get(0).getLineNbr());

    }

    @Override
    public void dataDownloadFailed() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Failed to load data, none or slow internet connection", Toast.LENGTH_LONG).show();


    }

}
