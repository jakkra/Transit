package se.jakobkrantz.transit.searching.fragments;/*
 * Created by krantz on 14-11-30.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Calendar;

import se.jakobkrantz.transit.apiasynctasks.DataDownloadListener;
import se.jakobkrantz.transit.apiasynctasks.SearchJourneysTask;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.base.BaseActivity;
import se.jakobkrantz.transit.base.FragmentEventListener;
import se.jakobkrantz.transit.searching.DetailedJourneyAdapter;
import se.jakobkrantz.transit.searching.FillDetailedHeaderHelper;
import se.jakobkrantz.transit.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.skanetrafikenAPI.TimeAndDateConverter;
import se.jakobkrantz.transit.utils.BundleConstants;

public class DetailedJourneyFragment extends Fragment implements DataDownloadListener, SwipeRefreshLayout.OnRefreshListener {
    private Journey journey;
    private DetailedJourneyAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;
    private FillDetailedHeaderHelper uiFiller;
    private int startId;
    private int endId;
    private String date;
    private String time;
    private ProgressWheel progressWheel;
    private FragmentEventListener eventListener;
    private FloatingActionButton mapButton;

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.detailed_journey_fragment, container, false);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_container);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorDividerBackground);
            progressWheel = (ProgressWheel) view.findViewById(R.id.progress_detail);
            recycleView = (RecyclerView) view.findViewById(R.id.detailed_recycle_view);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recycleView.setLayoutManager(mLayoutManager);
            recycleView.setHasFixedSize(true);
            recycleView.setItemAnimator(new DefaultItemAnimator());
            swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.setOnRefreshListener(this);
            uiFiller = new FillDetailedHeaderHelper(view);
            mapButton = (FloatingActionButton) view.findViewById(R.id.map_button);
            rootView = view;
        } else {
            //((ViewGroup) rootView.getParent()).removeView(rootView);

        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        startId = Integer.parseInt(args.getString(BundleConstants.FROM_STATION_ID));
        endId = Integer.parseInt(args.getString(BundleConstants.TO_STATION_ID));


        //TODO make nicer :p
        //For some reason  searching with the specific time does not return journeys for that exact departure time, we need to remove one minute from the journey time we want to search for.
        Calendar cal = TimeAndDateConverter.parseCalendarString(args.getString(BundleConstants.DEP_DATE) + "T" + args.getString(BundleConstants.DEP_TIME) + ":00");
        cal.add(Calendar.MINUTE, -1);
        String dayOfMonth = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        if (dayOfMonth.length() < 2) {
            dayOfMonth = 0 + dayOfMonth;
        }
        date = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + dayOfMonth;
        time = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);

        SearchJourneysTask task = new SearchJourneysTask();
        task.setDataDownloadListener(this);
        String url = Constants.getURL(startId, endId, date, time, 1);
        task.execute(url);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleConstants.FROM_STATION_ID, startId + "");
        outState.putString(BundleConstants.TO_STATION_ID, endId + "");

    }

    @Override
    public void dataDownloadedSuccessfully(Object data) {
        ArrayList<Journey> journeys = (ArrayList<Journey>) data;

        if (journeys.size() > 0) {
            journey = journeys.get(0);
            uiFiller.updateUI(journey);
            String resultKey = journey.getJourneyResultKey();
            if (resultKey != null) {
                mapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        b.putString(BundleConstants.JOURNEY_RESULT_KEY, journey.getJourneyResultKey());
                        eventListener.onEvent(BaseActivity.FragmentTypes.MAP, b);
                    }
                });
            }
            if (adapter == null) {

                adapter = new DetailedJourneyAdapter(journey);
                recycleView.setAdapter(adapter);
            } else {
                adapter.update(journey);

            }
        }
        swipeRefreshLayout.setRefreshing(false);
        progressWheel.setVisibility(View.GONE);


    }

    @Override
    public void dataDownloadFailed() {
        Toast.makeText(getActivity(), "Failed to load data, none or slow internet connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        SearchJourneysTask task = new SearchJourneysTask();
        task.setDataDownloadListener(this);
        task.execute(Constants.getURL(startId, endId, date, time, 1));

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


}
