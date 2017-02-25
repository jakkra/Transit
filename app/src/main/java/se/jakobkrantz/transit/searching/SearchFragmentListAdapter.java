package se.jakobkrantz.transit.searching;/*
 * Created by krantz on 14-11-19.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.skanetrafikenAPI.Station;

public class SearchFragmentListAdapter extends BaseAdapter {
    private final Activity activity;
    private List<Station> searchResults;
    private static LayoutInflater inflater = null;

    public SearchFragmentListAdapter(Activity activity, List<Station> initialList) {
        this.activity = activity;
        this.searchResults = initialList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.search_station_list_item, null);
        }
        TextView station = (TextView) vi.findViewById(R.id.station);
        station.setText(searchResults.get(position).getStationName());
        return vi;
    }

    public void setSearchResults(List<Station> searchResults) {
        this.searchResults = searchResults;
        notifyDataSetChanged();
    }

}
