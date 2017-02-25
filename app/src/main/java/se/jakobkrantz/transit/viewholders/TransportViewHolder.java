package se.jakobkrantz.transit.viewholders;/*
 * Created by krantz on 14-11-30.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import se.jakobkrantz.transit.app.R;

public class TransportViewHolder extends RecyclerView.ViewHolder {
    public TextView delayTime;
    public ImageView transportImage;
    public TextView lineNameNbr;
    public TextView towards;
    public TextView stopIDView;
    public TextView noteView;
    public ImageView clock;

    public TransportViewHolder(View view) {
        super(view);
        transportImage = (ImageView) view.findViewById(R.id.transport_image);
        lineNameNbr = (TextView) view.findViewById(R.id.bus_name);
        towards = (TextView) view.findViewById(R.id.towards_view);
        stopIDView = (TextView) view.findViewById(R.id.stop_view);
        noteView = (TextView) view.findViewById(R.id.note_view);
        delayTime = (TextView) view.findViewById(R.id.delay_time_detailed);
        clock = (ImageView) view.findViewById(R.id.clock_detailed);

    }
}
