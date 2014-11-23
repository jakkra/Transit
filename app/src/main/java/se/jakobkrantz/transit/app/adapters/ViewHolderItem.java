package se.jakobkrantz.transit.app.adapters;/*
 * Created by krantz on 14-11-21.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private final View v;
    private final ViewHolderClickListener listener;
    // each data item is just a string in this case
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
