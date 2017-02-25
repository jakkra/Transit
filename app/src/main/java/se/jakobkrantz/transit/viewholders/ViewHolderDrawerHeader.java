package se.jakobkrantz.transit.viewholders;/*
 * Created by jakkra on 2015-01-29.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import se.jakobkrantz.transit.app.R;

public class ViewHolderDrawerHeader extends RecyclerView.ViewHolder {
    private final View v;
    public TextView loadMore;

    public ViewHolderDrawerHeader(View v) {
        super(v);
        this.v = v;
        loadMore = (TextView) v.findViewById(R.id.res_load_more);
    }
}
