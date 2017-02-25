package se.jakobkrantz.transit.preferences;/*
 * Created by jakkra on 2015-02-07.
 */

import android.os.Bundle;

import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.base.BaseActivity;

public class SettingsActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.drawer_labels)[3]);
    }

}
