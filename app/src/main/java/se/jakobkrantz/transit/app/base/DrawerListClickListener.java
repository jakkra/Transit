package se.jakobkrantz.transit.app.base;/*
 * Created by krantz on 14-11-17.
 */

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import se.jakobkrantz.transit.app.reporting.ReportActivity;
import se.jakobkrantz.transit.app.searching.SearchActivity;

public class DrawerListClickListener implements ListView.OnItemClickListener {
    private final ListView drawerList;
    private final DrawerLayout layout;
    private String[] drawerLabels;
    private String title;
    private BaseActivity activity;
    private int lastPosition;

    public DrawerListClickListener(BaseActivity activity, ListView drawerList, DrawerLayout layout, String[] drawerLabels) {
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
//        if (position == lastPosition) {
//            Log.d("pos","" + position);
//            drawerList.setItemChecked(position, true);
//
//            layout.closeDrawer(drawerList);
//            return;
//        }
//        lastPosition = position;

        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(activity.getBaseContext(), SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                break;
            case 1:
                intent = new Intent(activity.getBaseContext(), ReportActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                break;
            default:
                intent = new Intent(activity.getBaseContext(), ReportActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
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
