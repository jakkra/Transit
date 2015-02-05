package se.jakobkrantz.transit.app.viewholders;/*
 * Created by krantz on 14-11-23.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.adapters.ViewHolderClickListener;
import se.jakobkrantz.transit.app.searching.FillUIHelper;

import java.util.List;

public class ViewHolderResult extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private View view;
    public ViewHolderClickListener listener;
    public TextView timeToArrival;
    public TextView timeBetween;
    public TextView nbrDepTime;
    public TextView message;
    public List<ImageView> transferIcons;
    public FillUIHelper fillUIHelper;

    public ViewHolderResult(View view, ViewHolderClickListener listener) {
        super(view);
        this.view = view;
        this.listener = listener;
        fillUIHelper = new FillUIHelper(view);
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
