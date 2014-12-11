package se.jakobkrantz.transit.app.drawer;/*
 * Created by krantz on 14-11-17.
 */

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import se.jakobkrantz.transit.app.MainActivity;
import se.jakobkrantz.transit.app.reporting.ReportActivity;

public class DrawerListClickListener implements ListView.OnItemClickListener {
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
                activity.replaceFragment(MainActivity.FragmentTypes.SEARCH_JOURNEY_FROM_TO, null);
                break;
            case 1:
                Intent intent = new Intent(activity, ReportActivity.class);
                activity.startActivity(intent);
                break;
            default:
                activity.replaceFragment(MainActivity.FragmentTypes.DUMMY, null);
                break;
        }


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
