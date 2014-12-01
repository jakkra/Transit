package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-30.
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
import android.widget.Toast;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.adapters.DetailedJourneyAdapter;
import se.jakobkrantz.transit.app.apiasynctasks.SearchJourneysTask;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.utils.BundleConstants;

import java.util.ArrayList;

public class DetailedJourneyFragment extends Fragment implements SearchJourneysTask.DataDownloadListener, SwipeRefreshLayout.OnRefreshListener {
    private Journey journey;
    private DetailedJourneyAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.detailed_journey_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_container);
        recycleView = (RecyclerView) view.findViewById(R.id.detailed_recycle_view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycleView.setLayoutManager(mLayoutManager);
//        recycleView.setHasFixedSize(true);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);

        Bundle args = getArguments();
        int startId = Integer.parseInt(args.getString(BundleConstants.FROM_STATION_ID));
        int endId = Integer.parseInt(args.getString(BundleConstants.TO_STATION_ID));

        SearchJourneysTask task = new SearchJourneysTask();
        task.setDataDownloadListener(this);
        task.execute(Constants.getURL(startId, endId, args.getString(BundleConstants.DEP_DATE), args.getString(BundleConstants.DEP_TIME), 1));

    }

    @Override
    public void dataDownloadedSuccessfully(Object data) {
        ArrayList<Journey> journeys = (ArrayList<Journey>) data;

        if (journeys.size() > 0) {
            journey = journeys.get(0);

            if (adapter == null) {
                adapter = new DetailedJourneyAdapter(journey);
                recycleView.setAdapter(adapter);
            }
        } else {
            //update adapter
        }
        swipeRefreshLayout.setRefreshing(false);


    }

    @Override
    public void dataDownloadFailed() {
        Toast.makeText(getActivity(), "Failed to load data, none or slow internet connection", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRefresh() {

    }
}
