package se.jakobkrantz.transit.app.disturbances;/*
 * Created by jakkra on 2015-02-03.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.viewholders.DisturbancesViewHolder;

public class DisturbanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String[] disturbs;

    public DisturbanceAdapter(String[] disturbs) {
        this.disturbs = disturbs;
        Log.e("distlength", disturbs.length + "");


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.disturbance_item, viewGroup, false);
        return new DisturbancesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof DisturbancesViewHolder) {
            DisturbancesViewHolder text = (DisturbancesViewHolder) viewHolder;
            Log.d("ösi", disturbs[i]);
            text.tv.setText(disturbs[i]);
        }
    }

    @Override
    public int getItemCount() {
        return disturbs.length;
    }

    public void updateDisturbances(String[] disturbs) {
        this.disturbs = disturbs;
        notifyDataSetChanged();
    }
}
