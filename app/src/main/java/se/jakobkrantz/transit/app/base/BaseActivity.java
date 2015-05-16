package se.jakobkrantz.transit.app.base;
/*
 * Created by krantz on 14-12-16.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.disturbances.DisturbancesActivity;
import se.jakobkrantz.transit.app.preferences.SettingsActivity;
import se.jakobkrantz.transit.app.reporting.ReportActivity;
import se.jakobkrantz.transit.app.searching.SearchActivity;


public class BaseActivity extends ActionBarActivity implements ListView.OnItemClickListener {
    protected static String LAST_FRAGMENT = "lastFragment"; //Remember last used fragment when app is closed.

    public enum FragmentTypes {
        SEARCH_STATION, SEARCH_JOURNEY_FROM_TO, SEARCH_RESULT, DETAILED_JOURNEY, TIME_AND_DATE_PICKER, TIME_SET, REPORT_FRAGMENT, MAP, DUMMY
    }

    private RecyclerView drawerList;
    private DrawerLayout drawerLayout;
    private String[] drawerListText;
    private DrawerListAdapter drawerListAdapter;
    private ActionBarDrawerToggle drawerToggle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        if (findViewById(R.id.content_frame) != null) {
            if (savedInstanceState != null) {
                return;
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeButtonEnabled(true);
        }
        drawerListText = getResources().getStringArray(R.array.drawer_labels);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        drawerList = (RecyclerView) findViewById(R.id.left_drawer);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        drawerList.setLayoutManager(mLayoutManager);
        drawerList.setItemAnimator(new DefaultItemAnimator());
        drawerListAdapter = new DrawerListAdapter(drawerListText, this);
        drawerList.setAdapter(drawerListAdapter);


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        //Disable drawer for Play Store version
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //drawerToggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {
        Intent intent;
        SharedPreferences prefs = getSharedPreferences(BaseActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int currentDrawerIndex = prefs.getInt("DRAWER_INDEX", 0);
        
        if (currentDrawerIndex != position) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("DRAWER_INDEX", position);
            editor.commit();
            switch (position) {
                case 0:
                    intent = new Intent(this, SearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(this, ReportActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    break;
                case 2:
                    intent = new Intent(this, DisturbancesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                default:
                    intent = new Intent(this, SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
            }
            setTitle(drawerListText[position]);
        }
        drawerLayout.closeDrawers();
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() < 2) {
                finish();
            }
            super.onBackPressed();

        }
    }


}
