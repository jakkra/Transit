package se.jakobkrantz.transit.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import se.jakobkrantz.transit.app.adapters.DrawerListAdapter;
import se.jakobkrantz.transit.app.drawer.DrawerListClickListener;
import se.jakobkrantz.transit.app.fragments.*;
import se.jakobkrantz.transit.app.utils.BundleConstants;


public class MainActivity extends ActionBarActivity implements SearchLocationFragment.StationSelectedListener, OnDetailedJourneySelectedListener {


    public enum FragmentTypes {
        SEARCH_STATION, SEARCH_JOURNEY_FROM_TO, SEARCH_RESULT, DETAILED_JOURNEY, DUMMY
    }

    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private String[] drawerListText;
    private DrawerListAdapter drawerListAdapter;
    private DrawerListClickListener drawerListClickListener;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerListText = getResources().getStringArray(R.array.drawer_labels);

        if (findViewById(R.id.content_frame) != null) {
            if (savedInstanceState != null) {
                return;
            }

        }
        replaceFragment(FragmentTypes.SEARCH_JOURNEY_FROM_TO, null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeButtonEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerListAdapter = new DrawerListAdapter(getApplicationContext(), drawerListText);
        drawerListClickListener = new DrawerListClickListener(this, drawerList, drawerLayout, drawerListText);
        drawerList.setAdapter(drawerListAdapter);
        drawerList.setOnItemClickListener(drawerListClickListener);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
    }

    //Should not be called by fragments, should instead be called by listeners implemented in this activity.
    public void replaceFragment(FragmentTypes searchStation, Bundle args) {
        switch (searchStation) {
            case SEARCH_JOURNEY_FROM_TO:
                MainFragment fragment = new MainFragment();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerList))
            drawerLayout.closeDrawer(drawerList);
        else
            super.onBackPressed();
    }
}
