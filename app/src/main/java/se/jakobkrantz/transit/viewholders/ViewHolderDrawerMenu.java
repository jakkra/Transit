package se.jakobkrantz.transit.viewholders;
/*
 * Created by jakkra on 2015-01-29.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import se.jakobkrantz.transit.app.R;

public class ViewHolderDrawerMenu extends RecyclerView.ViewHolder implements View.OnClickListener {
    public AdapterView.OnItemClickListener listener;
    public TextView tv;
    public ImageView icon;

    public ViewHolderDrawerMenu(View view, AdapterView.OnItemClickListener listener) {
        super(view);
        this.listener = listener;
        icon = (ImageView) view.findViewById(R.id.drawer_icon);
        tv = (TextView) view.findViewById(R.id.label);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.drawerItemRelativeLayout);
        relativeLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(null, v, getPosition(), 0);
    }
}