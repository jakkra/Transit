package se.jakobkrantz.transit.app.disturbances;/*
 * Created by jakkra on 2015-01-25.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.base.BaseActivity;
import se.jakobkrantz.transit.app.disturbances.fragments.DisturbancesFragment;
import se.jakobkrantz.transit.app.reporting.ReportActivity;

public class DisturbancesActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisturbancesFragment fragment = new DisturbancesFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.drawer_labels)[2]);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disturbance_button:
                Intent intent = new Intent(DisturbancesActivity.this, ReportActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                DisturbancesActivity.this.startActivity(intent);
                break;
        }
    }

}
