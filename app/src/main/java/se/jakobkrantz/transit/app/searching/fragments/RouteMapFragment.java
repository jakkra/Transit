package se.jakobkrantz.transit.app.searching.fragments;
/*
 * Created by jakkra on 2015-03-02.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.pnikosis.materialishprogress.ProgressWheel;
import se.jakobkrantz.transit.app.apiasynctasks.DataDownloadListener;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.apiasynctasks.SearchRouteMapTask;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Constants;
import se.jakobkrantz.transit.app.skanetrafikenAPI.CoordinateRoute;
import se.jakobkrantz.transit.app.utils.BundleConstants;

import java.util.ArrayList;
import java.util.List;

public class RouteMapFragment extends Fragment implements OnMapReadyCallback, DataDownloadListener, GoogleMap.OnCameraChangeListener {
    private GoogleMap map;
    private List<CoordinateRoute> journey;
    private boolean isMapFilled;
    private List<MarkerOptions> walkingMarkers;
    private List<Marker> showingWalkMarkers;
    private ProgressWheel progressWheel;
    private SupportMapFragment mapFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walkingMarkers = new ArrayList<MarkerOptions>();
        showingWalkMarkers = new ArrayList<Marker>();
        Bundle args = getArguments();
        String journeyKey = args.getString(BundleConstants.JOURNEY_RESULT_KEY);
        SearchRouteMapTask searchRouteMapTask = new SearchRouteMapTask();
        String pathUrl = Constants.getUrlJourneyPath(journeyKey, "0");
        searchRouteMapTask.setDataDownloadListener(this);
        searchRouteMapTask.execute(pathUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_f);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.getUiSettings().setZoomControlsEnabled(true);
        loadJourneyIntoMap(journey);
    }

    @Override
    public void dataDownloadedSuccessfully(Object data) {
        this.journey = (ArrayList<CoordinateRoute>) data;
        loadJourneyIntoMap(journey);
    }

    private void loadJourneyIntoMap(List<CoordinateRoute> j) {
        if (j != null && map != null && !isMapFilled) {
            isMapFilled = true;
            //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            long start = System.currentTimeMillis();
            PolylineOptions polylineOptions = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
            for (CoordinateRoute c : journey) {
                List<LatLng> latLngs = c.getLatLngs();
                for (int z = 0; z < latLngs.size() - 1; z++) {
                    LatLng src = latLngs.get(z);
                    LatLng dest = latLngs.get(z + 1);
                    polylineOptions.add(src, dest);
                }
            }
            map.addPolyline(polylineOptions);
            CoordinateRoute c;
            for (int i = 1; i < j.size(); i++) {
                c = j.get(i);
                if (!c.getLineNameNbr().contains("Gång")) {
                    map.addMarker(new MarkerOptions().title(c.getFrom().getStationName()).snippet("Byt till " + c.getLineName() + " " + c.getLineNbr()).position(c.getFrom().getLatLng()));
                    if (i != j.size() - 1) {
                        walkingMarkers.add(new MarkerOptions().title(c.getTo().getStationName()).snippet("Gång").position(c.getTo().getLatLng()));
                    }
                } else {
                    walkingMarkers.add(new MarkerOptions().title(c.getTo().getStationName()).snippet("Gång").position(c.getTo().getLatLng()));
                    walkingMarkers.add(new MarkerOptions().title(c.getFrom().getStationName()).snippet("Gång").position(c.getFrom().getLatLng()));

                }
            }
            map.addMarker(new MarkerOptions().title(j.get(0).getFrom().getStationName()).snippet("Avresa med " + j.get(0).getLineName() + " " + j.get(0).getLineNbr()).position(j.get(0).getFrom().getLatLng()));
            map.addMarker(new MarkerOptions().title(j.get(j.size() - 1).getTo().getStationName()).snippet("Destination").position(j.get(j.size() - 1).getTo().getLatLng()));

            LatLngBounds latLngBounds = new LatLngBounds.Builder().include(j.get(0).getFrom().getLatLng()).include(j.get(j.size() - 1).getTo().getLatLng()).build();
            mapFragment.getView().setVisibility(View.VISIBLE);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));
            map.setOnCameraChangeListener(this);
            progressWheel.setVisibility(View.INVISIBLE);
            long end = System.currentTimeMillis();
            Log.d("Drawing Time was", end - start + "");
        }
    }

    @Override
    public void dataDownloadFailed() {
        Toast.makeText(getActivity(), "Failed to load route, none or slow internet connection", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(getArguments());
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition.zoom > 10) {
            for (MarkerOptions m : walkingMarkers) {
                showingWalkMarkers.add(map.addMarker(m));
            }
        } else {
            for (Marker m : showingWalkMarkers) {
                m.remove();
            }
        }

    }
}
