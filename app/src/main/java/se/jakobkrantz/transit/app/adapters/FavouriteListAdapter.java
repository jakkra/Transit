package se.jakobkrantz.transit.app.adapters;/*
 * Created by krantz on 14-11-21.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.database.DatabaseTransitSQLite;
import se.jakobkrantz.transit.app.database.SimpleJourney;

import java.util.List;

public class FavouriteListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<SimpleJourney> simpleJourneys;
    private DatabaseTransitSQLite database;


    public FavouriteListAdapter(List<SimpleJourney> simpleJourneys, DatabaseTransitSQLite database) {
        this.simpleJourneys = simpleJourneys;
        this.database = database;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favourite_listview_item, viewGroup, false);
        ViewHolder.ViewHolderClickListener l = new ViewHolder.ViewHolderClickListener() {
            @Override
            public void onTextViewClick(View caller, int position) {

            }

            @Override
            public void onTextViewLongClick(View caller, int position) {
                remove(position);
            }
        };
        ViewHolder vh = new ViewHolder(v, l, i);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mTextView.setText(simpleJourneys.get(i).toString());

    }

    @Override
    public int getItemCount() {
        return simpleJourneys.size();
    }


    public void add(SimpleJourney simpleJourney) {
        simpleJourneys.add(simpleJourney);
        notifyItemInserted(simpleJourneys.size());
    }

    public void remove(int position) {
        SimpleJourney s = simpleJourneys.remove(position);
        Log.e("Removed fav: ", s.getFromStation() + " -> " + s.getToStation());
        database.deleteFavouriteJourney(s);
        notifyItemRemoved(position);
    }

}

