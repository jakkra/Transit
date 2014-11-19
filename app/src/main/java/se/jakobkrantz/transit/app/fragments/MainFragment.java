package se.jakobkrantz.transit.app.fragments;/*
 * Created by krantz on 14-11-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.apiasynctasks.SearchStationsTask;

public class MainFragment extends Fragment implements View.OnClickListener {
    TextView textView;
    EditText stationInput;
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
        stationInput = (EditText) view.findViewById(R.id.searchField);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        SearchStationsTask task = new SearchStationsTask(textView);
        String station = stationInput.getText().toString().replace(" ", "%20");

        task.execute("http://www.labs.skanetrafiken.se/v2.2/querystation.asp?inpPointfr=" +station);

    }
}
