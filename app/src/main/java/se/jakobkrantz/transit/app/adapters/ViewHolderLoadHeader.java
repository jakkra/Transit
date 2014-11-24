package se.jakobkrantz.transit.app.adapters;/*
 * Created by krantz on 14-11-24.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;

public class ViewHolderLoadHeader extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private final View v;
    private final ViewHolderClickListener listener;
    public TextView loadMore;

    public ViewHolderLoadHeader(View v, final ViewHolderClickListener listener) {
        super(v);
        this.v = v;
        this.listener = listener;
        loadMore = (TextView) v.findViewById(R.id.res_load_more);
        CardView cw = (CardView) v.findViewById(R.id.resultCardHeader);
        cw.setOnClickListener(this);
        cw.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("OnClick CardView", "yeah");
        listener.onViewClick(v, getPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        listener.onViewLongClick(v, getPosition());
        return true;
    }
}