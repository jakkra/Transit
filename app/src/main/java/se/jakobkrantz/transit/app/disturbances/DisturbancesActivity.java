package se.jakobkrantz.transit.app.disturbances;/*
 * Created by jakkra on 2015-01-25.
 */

import android.os.Bundle;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.base.BaseActivity;
import se.jakobkrantz.transit.app.disturbances.fragments.DisturbancesFragment;

public class DisturbancesActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisturbancesFragment fragment = new DisturbancesFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
    }

}
