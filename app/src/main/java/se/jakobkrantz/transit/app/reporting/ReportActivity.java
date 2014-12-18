package se.jakobkrantz.transit.app.reporting;/*
 * Created by krantz on 14-12-10.
 */

import android.os.Bundle;
import se.jakobkrantz.transit.app.base.BaseActivity;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.reporting.fragments.ReportFragment;

public class ReportActivity extends BaseActivity {




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReportFragment fragment = new ReportFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

    }



}
