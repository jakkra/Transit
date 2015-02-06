package se.jakobkrantz.transit.app.base;
/*
 * Created by krantz on 14-11-30.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.jakobkrantz.transit.app.R;

public class DrawerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int POSITION_HEADER = 0;
    public static final int POSITION_MENU = 1;

    private String[] options;
    private DrawerListClickListener listener;


    public DrawerListAdapter(String[] options, DrawerListClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == POSITION_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_list_header_item, viewGroup, false);
            return new ViewHolderDrawerHeader(v);
        } else if (viewType == POSITION_MENU) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_list_item, viewGroup, false);
            return new ViewHolderDrawerMenu(v, listener);
        }
        Log.e("DrawerListAdapter", "Should never be called");
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderDrawerHeader) {

        } else if (viewHolder instanceof ViewHolderDrawerMenu) {
            ViewHolderDrawerMenu menu = (ViewHolderDrawerMenu) viewHolder;
            menu.tv.setText(options[i]);
            menu.icon.setImageResource(getIconResource(i));
        }
    }

    private int getIconResource(int i) {
        switch (i) {
            case 0:
                return R.drawable.ic_search;
            case 1:
                return R.drawable.ic_report;
            case 2:
                return R.drawable.ic_deviations;
            default:
                return 0;
        }

    }


    @Override
    public int getItemViewType(int position) {
        // all  i to "i - 1" if header is wanted.
        //if (position == 0) {
        //    return POSITION_HEADER;
        //} else {
        return POSITION_MENU;
        // }
    }

    @Override
    public int getItemCount() {
        return options.length;
    }
}
