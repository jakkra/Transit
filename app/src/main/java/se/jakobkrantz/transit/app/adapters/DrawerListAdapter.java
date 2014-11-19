package se.jakobkrantz.transit.app.adapters;
/*
 * Created by krantz on 14-11-17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;


public class DrawerListAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    private String[] labels;

    public DrawerListAdapter(Context context, String[] drawerListText) {
        this.labels = drawerListText;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return labels.length;
    }

    @Override
    public Object getItem(int position) {
        return labels[position];

    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //Implement viewHolder http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    //If scrolling is slow (not smooth)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.drawer_list_item, null);
        }
        TextView loc = (TextView) vi.findViewById(R.id.label);
        loc.setText(labels[position]);
        return vi;
    }
}
