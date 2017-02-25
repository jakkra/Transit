package se.jakobkrantz.transit.preferences;/*
 * Created by jakkra on 2015-02-07.
 */

import android.os.Bundle;
import android.preference.PreferenceFragment;

import se.jakobkrantz.transit.app.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

    }
}
