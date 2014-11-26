package se.jakobkrantz.transit.app.adapters;/*
 * Created by krantz on 14-11-23.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;

import java.util.ArrayList;
import java.util.List;

public class ViewHolderResult extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private View view;
    public ViewHolderClickListener listener;
    public TextView timeToArrival;
    public TextView timeBetween;
    public TextView nbrDepTime;
    public TextView message;
    public List<ImageView> transferIcons;

    public ViewHolderResult(View view, ViewHolderClickListener listener) {
        super(view);
        this.view = view;
        this.listener = listener;
        timeToArrival = (TextView) view.findViewById(R.id.transport_name_nbr);
        timeBetween = (TextView) view.findViewById(R.id.time_between);
        nbrDepTime = (TextView) view.findViewById(R.id.nbr_dep_time);
        message = (TextView) view.findViewById(R.id.message);
        transferIcons = new ArrayList<ImageView>();
        ImageView iv = (ImageView) view.findViewById(R.id.imageView7);
        transferIcons.add(iv);
        iv = (ImageView) view.findViewById(R.id.imageView1);
        transferIcons.add(iv);
        iv = (ImageView) view.findViewById(R.id.imageView2);
        transferIcons.add(iv);
        iv = (ImageView) view.findViewById(R.id.imageView3);
        transferIcons.add(iv);
        iv = (ImageView) view.findViewById(R.id.imageView4);
        transferIcons.add(iv);
        iv = (ImageView) view.findViewById(R.id.imageView5);
        transferIcons.add(iv);
        iv = (ImageView) view.findViewById(R.id.imageView6);
        transferIcons.add(iv);
        CardView cw = (CardView) view.findViewById(R.id.resultCardItem);
        cw.setOnClickListener(this);
        cw.setOnLongClickListener(this);

    }

    @Override
    public void onClick(View v) {
        listener.onViewClick(v, getPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        listener.onViewClick(v, getPosition());
        return true;
    }
}
