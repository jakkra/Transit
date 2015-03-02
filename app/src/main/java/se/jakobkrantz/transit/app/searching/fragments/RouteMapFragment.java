package se.jakobkrantz.transit.app.searching.fragments;/*
 * Created by jakkra on 2015-03-02.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import se.jakobkrantz.transit.app.apiasynctasks.DataDownloadListener;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.apiasynctasks.SearchRouteMap;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.utils.BundleConstants;

import java.util.ArrayList;

public class RouteMapFragment extends Fragment implements OnMapReadyCallback, DataDownloadListener {
    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        String journeyKey = args.getString(BundleConstants.JOURNEY_RESULT_KEY);
        SearchRouteMap searchRouteMap = new SearchRouteMap();
        String pathUrl = Constants.getUrlJourneyPath(journeyKey, "0");
        Log.e("Path", pathUrl);
        searchRouteMap.execute(pathUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_f);
        map.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

    @Override
    public void dataDownloadedSuccessfully(Object data) {
        ArrayList<LatLng> latLngs = (ArrayList<LatLng>) data;

    }

    @Override
    public void dataDownloadFailed() {
        Toast.makeText(getActivity(), "Failed to load route, none or slow internet connection", Toast.LENGTH_LONG).show();
    }
}
