package se.jakobkrantz.transit.app.base;/*
 * Created by krantz on 14-11-17.
 */

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import se.jakobkrantz.transit.app.disturbances.DisturbancesActivity;
import se.jakobkrantz.transit.app.preferences.SettingsActivity;
import se.jakobkrantz.transit.app.reporting.ReportActivity;
import se.jakobkrantz.transit.app.searching.SearchActivity;

//TODO Bad design I know.
public class DrawerListClickListener implements ListView.OnItemClickListener {
    private final DrawerLayout layout;
    private String[] drawerLabels;
    private BaseActivity activity;

    public DrawerListClickListener(BaseActivity activity, DrawerLayout layout, String[] drawerLabels) {
        this.activity = activity;
        this.layout = layout;
        this.drawerLabels = drawerLabels;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {


        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(activity.getBaseContext(), SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                break;
            case 1:
                intent = new Intent(activity.getBaseContext(), ReportActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);

                break;
            case 2:
                intent = new Intent(activity.getBaseContext(), DisturbancesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                break;
            default:
                intent = new Intent(activity.getBaseContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                break;
        }

        setTitle(drawerLabels[position]);
        layout.closeDrawers();
    }

    public void setTitle(String title) {
        activity.getSupportActionBar().setTitle(title);
    }


}
