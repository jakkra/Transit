package se.jakobkrantz.transit.viewholders;/*
 * Created by jakkra on 2015-02-03.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import se.jakobkrantz.transit.app.R;

public class DisturbancesViewHolder extends RecyclerView.ViewHolder {
    public TextView tv;

    public DisturbancesViewHolder(View itemView) {
        super(itemView);
        tv = (TextView) itemView.findViewById(R.id.disturbance_list_item);


    }
}