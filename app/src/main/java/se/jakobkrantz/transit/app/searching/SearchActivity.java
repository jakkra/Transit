package se.jakobkrantz.transit.app.searching;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import se.jakobkrantz.transit.app.base.BaseActivity;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.base.FragmentEventListener;
import se.jakobkrantz.transit.app.searching.fragments.*;
import se.jakobkrantz.transit.app.utils.BundleConstants;


public class SearchActivity extends BaseActivity implements SearchLocationFragment.StationSelectedListener, OnDetailedJourneySelectedListener, FragmentEventListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onEvent(FragmentTypes.SEARCH_JOURNEY_FROM_TO, null);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.drawer_labels)[0]);

    }

    //Should not be called by fragments, should instead be called by listeners implemented in this activity.
    public void onEvent(FragmentTypes fragmentEvent, Bundle args) {
        switch (fragmentEvent) {
            case SEARCH_JOURNEY_FROM_TO:
                MainSearchFragment fragment = new MainSearchFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("Main").commit();
                break;
            case SEARCH_STATION:
                SearchLocationFragment searchFragment = new SearchLocationFragment();
                searchFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, searchFragment).addToBackStack("Search").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case SEARCH_RESULT:
                ResultFragment resultFragment = new ResultFragment();
                resultFragment.setOnJourneySelectedListener(this);
                resultFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, resultFragment).addToBackStack("Result").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case DETAILED_JOURNEY:
                DetailedJourneyFragment detailedJourneyFragment = new DetailedJourneyFragment();
                detailedJourneyFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, detailedJourneyFragment).addToBackStack("Detailed").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case TIME_AND_DATE_PICKER:
                FragmentManager fm = getSupportFragmentManager();
                TimeAndDatePickerDialogFragment timeAndDatePicker = new TimeAndDatePickerDialogFragment();
                timeAndDatePicker.show(fm, "Välj tid");
                break;
            default:
                DummyFragment dummyFragment = new DummyFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, dummyFragment).commit();
                break;
        }
    }

    @Override
    public void onStationSelected(Bundle args) {
        onEvent(FragmentTypes.SEARCH_JOURNEY_FROM_TO, args);
    }

    @Override
    public void onJourneySelected(String fromStationId, String toStationId, String depDate, String depTime) {
        Bundle args = new Bundle();
        args.putString(BundleConstants.FROM_STATION_ID, fromStationId);
        args.putString(BundleConstants.TO_STATION_ID, toStationId);
        args.putString(BundleConstants.DEP_DATE, depDate);
        args.putString(BundleConstants.DEP_TIME, depTime);
        onEvent(FragmentTypes.DETAILED_JOURNEY, args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

}
