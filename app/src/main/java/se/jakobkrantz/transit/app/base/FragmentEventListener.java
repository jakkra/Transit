package se.jakobkrantz.transit.app.base;
/*
 * Created by krantz on 14-12-18.
 */

import android.os.Bundle;

public interface FragmentEventListener {
    public void onEvent(BaseActivity.FragmentTypes fragmentEvent, Bundle args);
}
