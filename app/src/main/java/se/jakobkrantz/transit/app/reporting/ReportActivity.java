package se.jakobkrantz.transit.app.reporting;
/*
 * Created by krantz on 14-12-10.
 */

import android.os.Bundle;
import android.util.Log;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.base.BaseActivity;
import se.jakobkrantz.transit.app.base.FragmentEventListener;
import se.jakobkrantz.transit.app.reporting.fragments.ReportFragment;
import se.jakobkrantz.transit.app.searching.fragments.SearchLocationFragment;

public class ReportActivity extends BaseActivity implements SearchLocationFragment.StationSelectedListener, FragmentEventListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onEvent(FragmentTypes.REPORT_FRAGMENT, null);
    }


    @Override
    public void onStationSelected(Bundle args) {
        onEvent(FragmentTypes.REPORT_FRAGMENT, args);
    }

    @Override
    public void onEvent(FragmentTypes fragmentEvent, Bundle args) {
        switch (fragmentEvent) {
            case REPORT_FRAGMENT:
                ReportFragment fragmentReport = new ReportFragment();
                fragmentReport.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentReport).addToBackStack(null).commit();
                getSupportActionBar().setTitle(getResources().getStringArray(R.array.drawer_labels)[1]);
                break;
            case SEARCH_STATION:
                SearchLocationFragment fragment = new SearchLocationFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                break;
            default:
                Log.e("onEvent ReportActivity", "should not be called");
        }
    }

}
