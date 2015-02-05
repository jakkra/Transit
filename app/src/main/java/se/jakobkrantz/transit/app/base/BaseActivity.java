package se.jakobkrantz.transit.app.base;/*
 * Created by krantz on 14-12-16.
 */

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import se.jakobkrantz.transit.app.R;


public class BaseActivity extends ActionBarActivity {

    public enum FragmentTypes {
        SEARCH_STATION, SEARCH_JOURNEY_FROM_TO, SEARCH_RESULT, DETAILED_JOURNEY, TIME_AND_DATE_PICKER, TIME_SET, DUMMY
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
        drawerListAdapter = new DrawerListAdapter(drawerListText, new DrawerListClickListener(this, drawerLayout, drawerListText));
        drawerList.setAdapter(drawerListAdapter);


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerList)) {
            drawerLayout.closeDrawer(drawerList);
        } else {
            super.onBackPressed();
        }
    }


}
