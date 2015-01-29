package se.jakobkrantz.transit.app.base;
/*
 * Created by jakkra on 2015-01-29.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;


public class ViewHolderDrawerMenu extends RecyclerView.ViewHolder implements View.OnClickListener {
    public DrawerListClickListener listener;
    public TextView tv;

    public ViewHolderDrawerMenu(View view, DrawerListClickListener listener) {
        super(view);
        this.listener = listener;
        tv = (TextView) view.findViewById(R.id.label);
        tv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(null, null, getPosition(), 0);
    }
}