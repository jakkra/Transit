package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-22.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.adapters.ResultListAdapter;
import se.jakobkrantz.transit.app.apiasynctasks.SearchJourneysTask;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;

import java.util.ArrayList;


public class ResultFragment extends Fragment implements SearchJourneysTask.DataDownloadListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int SEARCH_RESULT_COUNT = 6;

    private Station fromStation;
    private Station toStation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;
    private ResultListAdapter resultListAdapter;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        fromStation = new Station(b.getString(MainFragment.FROM_STATION), Integer.parseInt(b.getString(MainFragment.FROM_STATION_ID)), Double.parseDouble(b.getString(MainFragment.FROM_STATION_LAT)), Double.parseDouble(b.getString(MainFragment.FROM_STATION_LONG)), b.getString(MainFragment.FROM_STATION_TYPE));
        toStation = new Station(b.getString(MainFragment.TO_STATION), Integer.parseInt(b.getString(MainFragment.TO_STATION_ID)), Double.parseDouble(b.getString(MainFragment.TO_STATION_LAT)), Double.parseDouble(b.getString(MainFragment.TO_STATION_LONG)), b.getString(MainFragment.TO_STATION_TYPE));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.result_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        recycleView = (RecyclerView) view.findViewById(R.id.recycle_view);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setHasFixedSize(true);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        SearchJourneysTask task = new SearchJourneysTask();
        task.setDataDownloadListener(this);
        task.execute(Constants.getURL(fromStation.getStationId(), toStation.getStationId(), Constants.getCurrentDate(), Constants.getCurrentTime(), SEARCH_RESULT_COUNT));
    }

    @Override
    public void onRefresh() {
        //TODO adapter will also do notifyChangeALL
        Toast.makeText(getActivity(), "Is refreshing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dataDownloadedSuccessfully(Object data) {
        ArrayList<Journey> journeys = (ArrayList<Journey>) data;
        swipeRefreshLayout.setRefreshing(false);
        if (resultListAdapter == null) {

            resultListAdapter = new ResultListAdapter(journeys, new ResultListListener());
            recycleView.setAdapter(resultListAdapter);
        } else {
            resultListAdapter.addJourneys(journeys);
        }
    }

    @Override
    public void dataDownloadFailed() {
        Toast.makeText(getActivity(), "Failed to load data, none or slow internet connection", Toast.LENGTH_LONG).show();
    }

    private class ResultListListener implements ResultListAdapter.OnResultItemClicked {
        @Override
        public void onResultClicked(Journey j) {
            Toast.makeText(getActivity(), "Result item clicked " + j.getStartStation() + " -> " + j.getEndStation(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResultLongClickListener(Journey j) {
            Toast.makeText(getActivity(), "Result item long click " + j.getStartStation() + " -> " + j.getEndStation(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onLoadMoreResults(boolean b) {
            //Load earlier
            if (b) {

            } else { //Load later

            }

        }


    }
}
