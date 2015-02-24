package se.jakobkrantz.transit.app.viewholders;/*
 * Created by krantz on 14-11-21.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.base.ViewHolderClickListener;

public class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private final View v;
    private final ViewHolderClickListener listener;
    public TextView favTextView;

    public ViewHolderItem(View v, ViewHolderClickListener listener) {
        super(v);
        this.v = v;
        this.listener = listener;
        favTextView = (TextView) v.findViewById(R.id.info_text);
        favTextView.setOnClickListener(this);
        favTextView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            listener.onViewClick(v, getPosition());
        } else {

        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof TextView) {
            listener.onViewLongClick(v, getPosition());
            return true;
        } else {
            return false;
        }
    }
}
