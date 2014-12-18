package se.jakobkrantz.transit.app.searching;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import se.jakobkrantz.transit.app.base.BaseActivity;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.searching.fragments.*;
import se.jakobkrantz.transit.app.utils.BundleConstants;


public class SearchActivity extends BaseActivity implements SearchLocationFragment.StationSelectedListener, OnDetailedJourneySelectedListener {
    public enum FragmentTypes {
        SEARCH_STATION, SEARCH_JOURNEY_FROM_TO, SEARCH_RESULT, DETAILED_JOURNEY, DUMMY
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replaceFragment(FragmentTypes.SEARCH_JOURNEY_FROM_TO, null);
    }

    //Should not be called by fragments, should instead be called by listeners implemented in this activity.
    public void replaceFragment(FragmentTypes searchStation, Bundle args) {
        switch (searchStation) {
            case SEARCH_JOURNEY_FROM_TO:
                MainSearchFragment fragment = new MainSearchFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                break;
            case SEARCH_STATION:
                SearchLocationFragment searchFragment = new SearchLocationFragment();
                searchFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, searchFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case SEARCH_RESULT:
                ResultFragment resultFragment = new ResultFragment();
                resultFragment.setOnJourneySelectedListener(this);
                resultFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, resultFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case DETAILED_JOURNEY:

                DetailedJourneyFragment detailedJourneyFragment = new DetailedJourneyFragment();
                detailedJourneyFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, detailedJourneyFragment).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            default:

                DummyFragment dummyFragment = new DummyFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, dummyFragment).commit();
                break;
        }
    }

    @Override
    public void onStationSelected(Bundle args) {
        replaceFragment(FragmentTypes.SEARCH_JOURNEY_FROM_TO, args);
    }

    @Override
    public void onJourneySelected(String fromStationId, String toStationId, String depDate, String depTime) {
        Bundle args = new Bundle();
        args.putString(BundleConstants.FROM_STATION_ID, fromStationId);
        args.putString(BundleConstants.TO_STATION_ID, toStationId);
        args.putString(BundleConstants.DEP_DATE, depDate);
        args.putString(BundleConstants.DEP_TIME, depTime);
        replaceFragment(FragmentTypes.DETAILED_JOURNEY, args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
