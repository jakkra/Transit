package se.jakobkrantz.transit.app.searching.fragments;
/*
 * Created by krantz on 14-11-17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.pnikosis.materialishprogress.ProgressWheel;
import se.jakobkrantz.transit.app.apiasynctasks.DataDownloadListener;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.base.FragmentEventListener;
import se.jakobkrantz.transit.app.searching.FillUIHelper;
import se.jakobkrantz.transit.app.searching.SearchActivity;
import se.jakobkrantz.transit.app.searching.FavouriteListAdapter;
import se.jakobkrantz.transit.app.apiasynctasks.SearchJourneysTask;
import se.jakobkrantz.transit.app.database.DatabaseTransitSQLite;
import se.jakobkrantz.transit.app.database.SimpleJourney;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;
import se.jakobkrantz.transit.app.utils.BundleConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainSearchFragment extends Fragment implements View.OnClickListener, FavouriteListAdapter.OnItemChangeListener, DataDownloadListener, TimeAndDatePickerDialogFragment.OnTimeSetListener {
    public static final int NBR_OF_LIST_ITEM_TO_SHOW = 10;

    private FragmentEventListener eventListener;
    private TextView fromStation;
    private TextView toStation;
    private Button searchButton;
    private Button favButton;
    private DatabaseTransitSQLite database;
    private RecyclerView listView;
    private FavouriteListAdapter favListAdapter;
    private ProgressWheel progressBar;
    private FillUIHelper fillUIHelper;
    private Date searchDate;
    private ImageButton setTimeButton;
    private RelativeLayout relativeSwapStation;
    private Bundle initBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        database = new DatabaseTransitSQLite(getActivity());
        initBundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        toStation = (TextView) view.findViewById(R.id.text_view_to_station);
        relativeSwapStation = (RelativeLayout) view.findViewById(R.id.relative_swap);
        relativeSwapStation.setOnClickListener(this);
        fromStation = (TextView) view.findViewById(R.id.text_view_from_station);
        searchButton = (Button) view.findViewById(R.id.gcmButton);
        favButton = (Button) view.findViewById(R.id.favourite_button);
        setTimeButton = (ImageButton) view.findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(this);
        listView = (RecyclerView) view.findViewById(R.id.listView);
        progressBar = (ProgressWheel) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        fillUIHelper = new FillUIHelper(view);
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
                if (fromStation.getText().length() > 1 && toStation.getText().length() > 1) {

                    Bundle b = initBundle;
                    if (b == null) {
                        b = new Bundle();
                    }
                    SimpleJourney s = database.getSimpleJourneyFromRecentOrFavs(fromStation.getText().toString(), toStation.getText().toString());
                    Station s1 = s.getFromStation();
                    Station s2 = s.getToStation();

                    if (s.getFromStation() == null && s.getToStation() == null) {
                        s1 = new Station(b.getString(BundleConstants.FROM_STATION), Integer.parseInt(b.getString(BundleConstants.FROM_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LONG)), b.getString(BundleConstants.FROM_STATION_TYPE));
                        s2 = new Station(b.getString(BundleConstants.TO_STATION), Integer.parseInt(b.getString(BundleConstants.TO_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LONG)), b.getString(BundleConstants.TO_STATION_TYPE));
                        s = new SimpleJourney(s1, s2);
                    } else if (s1 == null) {
                        s1 = new Station(b.getString(BundleConstants.FROM_STATION), Integer.parseInt(b.getString(BundleConstants.FROM_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LONG)), b.getString(BundleConstants.FROM_STATION_TYPE));
                        s.setFromStation(s1);
                    } else if (s2 == null) {
                        s2 = new Station(b.getString(BundleConstants.TO_STATION), Integer.parseInt(b.getString(BundleConstants.TO_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LONG)), b.getString(BundleConstants.TO_STATION_TYPE));
                        s.setToStation(s2);
                    }
                    if (database.addStationFavPair(s)) {
                        favListAdapter.addFavourite(s);
                    }
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromStation.getText().length() > 1 && toStation.getText().length() > 1) {

                    Bundle b = initBundle;
                    if (b == null) {
                        b = new Bundle();
                    }
                    List<Station> recentSearches = new ArrayList<Station>();
                    SimpleJourney s = database.getSimpleJourneyFromRecentOrFavs(fromStation.getText().toString(), toStation.getText().toString());
                    Station s1 = s.getFromStation();
                    Station s2 = s.getToStation();
                    if (s.getFromStation() == null && s.getToStation() == null) {
                        s1 = new Station(b.getString(BundleConstants.FROM_STATION), Integer.parseInt(b.getString(BundleConstants.FROM_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LONG)), b.getString(BundleConstants.FROM_STATION_TYPE));
                        s2 = new Station(b.getString(BundleConstants.TO_STATION), Integer.parseInt(b.getString(BundleConstants.TO_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LONG)), b.getString(BundleConstants.TO_STATION_TYPE));
                        recentSearches.add(s1);
                        recentSearches.add(s2);
                        s = new SimpleJourney(s1, s2);
                    } else if (s1 == null) {
                        s1 = new Station(b.getString(BundleConstants.FROM_STATION), Integer.parseInt(b.getString(BundleConstants.FROM_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LONG)), b.getString(BundleConstants.FROM_STATION_TYPE));
                        recentSearches.add(s1);
                        recentSearches.add(s.getToStation());
                        recentSearches.add(s1);

                        s.setFromStation(s1);
                    } else if (s2 == null) {
                        s2 = new Station(b.getString(BundleConstants.TO_STATION), Integer.parseInt(b.getString(BundleConstants.TO_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LONG)), b.getString(BundleConstants.TO_STATION_TYPE));
                        recentSearches.add(s2);
                        recentSearches.add(s.getFromStation());
                        s.setToStation(s2);
                    }

                    b = new Bundle();
                    b.putString(BundleConstants.FROM_STATION, s1.getStationName());
                    b.putString(BundleConstants.FROM_STATION_ID, Integer.toString(s1.getStationId()));
                    b.putString(BundleConstants.FROM_STATION_LONG, Double.toString(s1.getLongitude()));
                    b.putString(BundleConstants.FROM_STATION_LAT, Double.toString(s1.getLatitude()));
                    b.putString(BundleConstants.FROM_STATION_TYPE, s1.getType());
                    b.putString(BundleConstants.FROM_STATION_SEARCHED, s1.getTimeSearched());

                    b.putString(BundleConstants.TO_STATION, s2.getStationName());
                    b.putString(BundleConstants.TO_STATION_ID, Integer.toString(s2.getStationId()));
                    b.putString(BundleConstants.TO_STATION_LONG, Double.toString(s2.getLongitude()));
                    b.putString(BundleConstants.TO_STATION_LAT, Double.toString(s2.getLatitude()));
                    b.putString(BundleConstants.TO_STATION_TYPE, s2.getType());
                    b.putString(BundleConstants.TO_STATION_SEARCHED, s2.getTimeSearched());

                    if (searchDate != null) {
                        b.putString(BundleConstants.SET_TIME_AND_DATE, new SimpleDateFormat("yyMMdd HH:mm").format(searchDate));
                    }


                    database.addRecentJourneySearch(s);
                    database.addStationsToRecent(recentSearches);
                    favListAdapter.addRecentJourney(s);
                    eventListener.onEvent(SearchActivity.FragmentTypes.SEARCH_RESULT, b);

                }
            }
        });

        List<SimpleJourney> j = database.getRecentJourneys(1);
        if (j.size() > 0) {
            SearchJourneysTask task = new SearchJourneysTask();
            task.setDataDownloadListener(this);
            task.execute(Constants.getURL(j.get(0).getFromStation().getStationId(), j.get(0).getToStation().getStationId(), 1));
            fromStation.setText(j.get(0).getFromStation().toString());
            toStation.setText(j.get(0).getToStation().toString());
        }
        fillStationsText(initBundle);
    }


    private void fillStationsText(Bundle args) {
        if (args != null) {
            String from = args.getString(BundleConstants.FROM_STATION);
            String to = args.getString(BundleConstants.TO_STATION);
            if (from != null) fromStation.setText(from);
            if (to != null) toStation.setText(to);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setTimeButton) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            TimeAndDatePickerDialogFragment dialog = new TimeAndDatePickerDialogFragment();
            dialog.setTargetFragment(this, 1);
            dialog.show(fm, "TimeAndDatePicker");

        } else if (v.getId() == R.id.relative_swap) {
            swapFromAndToStation();
        } else {

            Bundle args = initBundle;
            if (args == null) args = new Bundle();
            if (v.getId() == R.id.text_view_from_station) {
                args.putString(BundleConstants.SOURCE, BundleConstants.SOURCE_FROM_STATION);
            }
            if (v.getId() == R.id.text_view_to_station) {
                args.putString(BundleConstants.SOURCE, BundleConstants.SOURCE_TO_STATION);
            }
            args.putString(BundleConstants.FROM_STATION, fromStation.getText().toString());
            args.putString(BundleConstants.TO_STATION, toStation.getText().toString());
            eventListener.onEvent(SearchActivity.FragmentTypes.SEARCH_STATION, args);
        }
    }

    /**
     * Swaps the text between from and to. Also switches the data corresponding to each of them.
     */
    private void swapFromAndToStation() {
        String s1 = fromStation.getText().toString();
        String s2 = toStation.getText().toString();
        fromStation.setText(s2);
        toStation.setText(s1);
        Bundle before = initBundle;
        if (before == null) {
            return;
        }
        Bundle after = new Bundle();
        after.putString(BundleConstants.FROM_STATION, before.getString(BundleConstants.TO_STATION));
        after.putString(BundleConstants.FROM_STATION_ID, before.getString(BundleConstants.TO_STATION_ID));
        after.putString(BundleConstants.FROM_STATION_LONG, before.getString(BundleConstants.TO_STATION_LONG));
        after.putString(BundleConstants.FROM_STATION_LAT, before.getString(BundleConstants.TO_STATION_LAT));
        after.putString(BundleConstants.FROM_STATION_TYPE, before.getString(BundleConstants.TO_STATION_TYPE));
        after.putString(BundleConstants.FROM_STATION_SEARCHED, before.getString(BundleConstants.TO_STATION_SEARCHED));

        after.putString(BundleConstants.TO_STATION, before.getString(BundleConstants.FROM_STATION));
        after.putString(BundleConstants.TO_STATION_ID, before.getString(BundleConstants.FROM_STATION_ID));
        after.putString(BundleConstants.TO_STATION_LONG, before.getString(BundleConstants.FROM_STATION_ID));
        after.putString(BundleConstants.TO_STATION_LAT, before.getString(BundleConstants.FROM_STATION_ID));
        after.putString(BundleConstants.TO_STATION_TYPE, before.getString(BundleConstants.FROM_STATION_ID));
        after.putString(BundleConstants.TO_STATION_SEARCHED, before.getString(BundleConstants.FROM_STATION_ID));

        after.putString(BundleConstants.SET_TIME_AND_DATE, before.getString(BundleConstants.SET_TIME_AND_DATE));
        after.putString(BundleConstants.DEP_DATE, before.getString(BundleConstants.DEP_DATE));
        after.putString(BundleConstants.SOURCE, before.getString(BundleConstants.SOURCE));
        after.putString(BundleConstants.SOURCE_FROM_STATION, before.getString(BundleConstants.SOURCE_TO_STATION));
        after.putString(BundleConstants.SOURCE_TO_STATION, before.getString(BundleConstants.SOURCE_FROM_STATION));
        this.initBundle = after;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_recent_searches:
                database.clearRecentJourneySearches();
                favListAdapter.clearAllSearches();
                fromStation.setText("");
                toStation.setText("");
                return true;
            case R.id.action_clear_favourites:
                database.clearAllFavourites();
                favListAdapter.clearAllFavourites();
                fromStation.setText("");
                toStation.setText("");
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
        if (fromStation.getText().length() > 1 && toStation.getText().length() > 1) {
            outState.putString(BundleConstants.TO_STATION, toStation.getText().toString());
            outState.putString(BundleConstants.FROM_STATION, fromStation.getText().toString());

        }
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
        Log.e("URL", url);
        task.execute(url);
        fromStation.setText(s.getFromStation().toString());
        toStation.setText(s.getToStation().toString());
    }

    @Override
    public void onItemLongClickListener(SimpleJourney s) {
        //Toast.makeText(getActivity(), s.getFromStation() + " -> " + s.getToStation(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFavouriteItemAdded(SimpleJourney s) {
        //List was full, more than 10? favourites, deletes oldest, why? Because 10 is enough! :)
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
        if (journeyList.size() > 0) {
            fillUIHelper.updateUI(journeyList.get(0));

        } else {
            //TODO show better message if no routes found.
            Toast.makeText(getActivity(), "No Journeys found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void dataDownloadFailed() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Failed to load data, none or slow internet connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the listener interface. If not, it throws an exception.
        try {
            eventListener = (FragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StationSelectedListener");
        }
    }

    @Override
    public void onTimeSet(Date date) {
        searchDate = date;

    }
}
