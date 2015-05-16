package se.jakobkrantz.transit.app.searching.fragments;/*
 * Created by krantz on 14-11-22.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import com.pnikosis.materialishprogress.ProgressWheel;
import org.w3c.dom.Text;
import se.jakobkrantz.transit.app.apiasynctasks.DataDownloadListener;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.searching.ResultListAdapter;
import se.jakobkrantz.transit.app.apiasynctasks.SearchJourneysTask;
import se.jakobkrantz.transit.app.searching.OnDetailedJourneySelectedListener;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Station;
import se.jakobkrantz.transit.app.skanetrafikenAPI.TimeAndDateConverter;
import se.jakobkrantz.transit.app.utils.BundleConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ResultFragment extends Fragment implements DataDownloadListener, SwipeRefreshLayout.OnRefreshListener {
    private Station fromStation;
    private Station toStation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycleView;
    private ResultListAdapter resultListAdapter;
    private OnDetailedJourneySelectedListener listener;
    private Date searchDate;
    private ProgressWheel progressWheel;
    private TextView tvFromStation;
    private TextView tvToStation;
    private TextView tvDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        fromStation = new Station(b.getString(BundleConstants.FROM_STATION), Integer.parseInt(b.getString(BundleConstants.FROM_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.FROM_STATION_LONG)), b.getString(BundleConstants.FROM_STATION_TYPE));
        toStation = new Station(b.getString(BundleConstants.TO_STATION), Integer.parseInt(b.getString(BundleConstants.TO_STATION_ID)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LAT)), Double.parseDouble(b.getString(BundleConstants.TO_STATION_LONG)), b.getString(BundleConstants.TO_STATION_TYPE));
        String date = b.getString(BundleConstants.SET_TIME_AND_DATE);
        try {

            if (date != null) {
                searchDate = new SimpleDateFormat("yyMMdd HH:mm").parse(b.getString(BundleConstants.SET_TIME_AND_DATE));
            } else {
                searchDate = new SimpleDateFormat("yyMMdd HH:mm").parse(new SimpleDateFormat("yyMMdd HH:mm").format(new Date()));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.result_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recycleView = (RecyclerView) view.findViewById(R.id.recycle_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setHasFixedSize(true);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout.setRefreshing(true);
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_result);
        tvFromStation = (TextView) view.findViewById(R.id.result_from_textview);
        tvToStation = (TextView) view.findViewById(R.id.result_to_textview);
        tvDate = (TextView) view.findViewById(R.id.result_date_textview);
        tvFromStation.setText("Från: " + fromStation.toString());
        tvToStation.setText("Till: " + toStation.toString());
        tvDate.setText(new SimpleDateFormat("dd/MM yyyy").format(searchDate));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout.setOnRefreshListener(this);
        SearchJourneysTask task = new SearchJourneysTask();
        task.setDataDownloadListener(this);
        String date;
        String time;
        if (searchDate != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(searchDate);
            time = new SimpleDateFormat("HH:mm").format(searchDate);
        } else {
            date = Constants.getCurrentDate();
            time = Constants.getCurrentTime();
        }

        task.execute(Constants.getURL(fromStation.getStationId(), toStation.getStationId(), date, time, ResultListAdapter.NBR_ITEMS_PER_LOAD));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BundleConstants.FROM_STATION_ID, fromStation.getStationId());
        outState.putInt(BundleConstants.TO_STATION_ID, toStation.getStationId());
    }

    /**
     * Reloads {SEARCH_RESULT_COUNT} results in the list. If the list contains more, they are removed totally.
     */
    @Override
    public void onRefresh() {
        SearchJourneysTask task = new SearchJourneysTask();
        task.setDataDownloadListener(this);
        String date;
        String time;
        if (searchDate != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").format(searchDate);
            time = new SimpleDateFormat("HH:mm").format(searchDate);
        } else {
            date = Constants.getCurrentDate();
            time = Constants.getCurrentTime();
        }
        task.execute(Constants.getURL(fromStation.getStationId(), toStation.getStationId(), date, time, ResultListAdapter.NBR_ITEMS_PER_LOAD));

    }

    @Override
    public void dataDownloadedSuccessfully(Object data) {
        ArrayList<Journey> journeys = (ArrayList<Journey>) data;
        swipeRefreshLayout.setRefreshing(false);
        if (resultListAdapter == null) {
            resultListAdapter = new ResultListAdapter(journeys, new ResultListListener());
            recycleView.setAdapter(resultListAdapter);
        } else {
            resultListAdapter.setJourneys(journeys);
            //Fragment reopened, means recycleView is initialised again
            recycleView.setAdapter(resultListAdapter);
        }
        progressWheel.setVisibility(View.GONE);

    }

    @Override
    public void dataDownloadFailed() {
        //TODO handle this better
        Toast.makeText(getActivity(), "Failed to load data, none or slow internet connection", Toast.LENGTH_LONG).show();
    }

    public void setOnJourneySelectedListener(OnDetailedJourneySelectedListener listener) {
        this.listener = listener;
    }

    private class ResultListListener implements ResultListAdapter.OnResultItemClicked {
        @Override
        public void onResultClicked(Journey j) {
            if (listener != null) {
                listener.onJourneySelected(Integer.toString(fromStation.getStationId()), Integer.toString(toStation.getStationId()), TimeAndDateConverter.getDate(j.getDepDateTime()), TimeAndDateConverter.formatTime(j.getDepDateTime()));
            }
        }

        @Override
        public void onResultLongClickListener(Journey j) {
            Toast.makeText(getActivity(), "Result item long click " + j.getStartStation() + " -> " + j.getEndStation(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onLoadMoreResults(final boolean b, String dateAndTime) {
            //Load earlier
            String date = TimeAndDateConverter.getDate(dateAndTime);
            String time = TimeAndDateConverter.formatTime(dateAndTime);
            SearchJourneysTask task = new SearchJourneysTask();
            task.setDataDownloadListener(new DataDownloadListener() {
                @Override
                public void dataDownloadedSuccessfully(Object data) {
                    ArrayList<Journey> journeys = (ArrayList<Journey>) data;
                    swipeRefreshLayout.setRefreshing(false);
                    resultListAdapter.addJourneys(journeys, b);
                }

                @Override
                public void dataDownloadFailed() {
                    //TODO handle this better
                    Toast.makeText(getActivity(), "Failed to load data, none or slow internet connection", Toast.LENGTH_LONG).show();
                }
            });
            if (b) {
                task.execute(Constants.getURLPreviousResults(fromStation.getStationId(), toStation.getStationId(), date, time, ResultListAdapter.NBR_ITEMS_PER_LOAD));
            } else { //Load later
                task.execute(Constants.getURL(fromStation.getStationId(), toStation.getStationId(), date, time, ResultListAdapter.NBR_ITEMS_PER_LOAD));
            }

        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
