package se.jakobkrantz.transit.app.drawer;/*
 * Created by krantz on 14-11-17.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import se.jakobkrantz.transit.app.MainActivity;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.fragments.DummyFragment;
import se.jakobkrantz.transit.app.fragments.MainFragment;

public class DrawerListClickListener implements ListView.OnItemClickListener {
    private Context context;
    private final ListView drawerList;
    private final DrawerLayout layout;
    private String[] drawerLabels;
    private String title;
    private MainActivity activity;

    public DrawerListClickListener(MainActivity activity, ListView drawerList, DrawerLayout layout, String[] drawerLabels) {
        this.activity = activity;
        this.drawerList = drawerList;
        this.layout = layout;
        this.drawerLabels = drawerLabels;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new MainFragment();
                break;
            default:
                fragment = new DummyFragment();
                break;
        }


        //Bundle args = new Bundle();
        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        //fragment.setArguments(args);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        setTitle(drawerLabels[position]);
        layout.closeDrawer(drawerList);

    }

    public void setTitle(String title) {
        this.title = title;
        activity.getSupportActionBar().setTitle(title);
    }
}
