package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-19.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.apiasynctasks.SearchStationsTask;


/**
 * Class used when clicked on choose location to travel to/fram
 */
public class SearchLocationFragment extends Fragment implements View.OnClickListener {
    private SearchStationsTask searchTask;
    private ListView listView;


    public SearchLocationFragment() {
        searchTask = new SearchStationsTask(null);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.search_location_fragment, container, false);
        return view;
    }
    @Override
    public void onClick(View v) {
        searchTask.execute("url");//Changed to url
    }


}
