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
public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public final View v;
    private int positon;
    private final ViewHolderClickListener listener;
    // each data item is just a string in this case
    public TextView mTextView;

    public ViewHolder(View v, ViewHolderClickListener listener, int position) {
        super(v);
        this.v = v;
        this.listener = listener;
        this.positon = position;
        mTextView = (TextView) v.findViewById(R.id.info_text);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            listener.onTextViewClick(v, positon);
        } else {

        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof TextView) {
            listener.onTextViewLongClick(v, positon);
            return true;
        } else {
            return false;
        }
    }

    public static interface ViewHolderClickListener {
        public void onTextViewClick(View caller, int position);

        public void onTextViewLongClick(View caller, int position);
    }
}
