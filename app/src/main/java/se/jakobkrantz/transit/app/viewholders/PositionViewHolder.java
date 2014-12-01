package se.jakobkrantz.transit.app.viewholders;/*
 * Created by krantz on 14-11-30.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;

public class PositionViewHolder extends RecyclerView.ViewHolder {
    public ImageView circle;
    public TextView depTime;
    public TextView arrTime;
    public TextView position;

    public PositionViewHolder(View view) {
        super(view);
        circle = (ImageView) view.findViewById(R.id.circleView);
        arrTime = (TextView) view.findViewById(R.id.arrivall_time);
        depTime = (TextView) view.findViewById(R.id.dep_time);
        position = (TextView) view.findViewById(R.id.position);
    }
}
