package se.jakobkrantz.transit.app.disturbances.fragments;/*
 * Created by jakkra on 2015-01-25.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.jakobkrantz.transit.app.R;

public class DisturbancesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disturbances, container, false);


        return view;
    }
}
