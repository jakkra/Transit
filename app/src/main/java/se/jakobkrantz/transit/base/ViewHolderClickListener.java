package se.jakobkrantz.transit.base;/*
 * Created by krantz on 14-11-23.
 */

import android.view.View;

public interface ViewHolderClickListener {
    public void onViewClick(View caller, int position);

    public void onViewLongClick(View caller, int position);
}
