package se.jakobkrantz.transit.app.adapters;/*
 * Created by krantz on 14-11-30.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.fragments.FillUIHelper;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.RouteLink;
import se.jakobkrantz.transit.app.skanetrafikenAPI.TimeAndDateConverter;
import se.jakobkrantz.transit.app.viewholders.PositionViewHolder;
import se.jakobkrantz.transit.app.viewholders.TransportViewHolder;

public class DetailedJourneyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int POSITION_VIEW = 2;
    public static final int TRANSPORT_VIEW = 4;

    private Journey journey;


    public DetailedJourneyAdapter(Journey journey) {
        this.journey = journey;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == POSITION_VIEW) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.journey_position_item, viewGroup, false);
            return new PositionViewHolder(v);
        } else if (viewType == TRANSPORT_VIEW) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.journey_transport_item, viewGroup, false);
            return new TransportViewHolder(v);
        } else {
            Log.e("WRONG TYPE, not supported in this recycleView", "Returning null!");
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof PositionViewHolder) {
            RouteLink r = journey.getRouteLinks().get(i / 2);
            PositionViewHolder holder = ((PositionViewHolder) viewHolder);
            if (i != 0) {
                holder.depTime.setText(journey.getRouteLinks().get((i / 2) - 1).getArrDateTime()); //Change to just HH:mm
            }
            holder.depTime.setText(TimeAndDateConverter.formatTime(r.getDepDateTime()));
            holder.position.setText(r.getFromStation().toString());
        } else if (viewHolder instanceof TransportViewHolder) {
            RouteLink r = journey.getRouteLinks().get(i / 2);
            TransportViewHolder holder = ((TransportViewHolder) viewHolder);
            holder.lineNameNbr.setText(r.getTransportModeName() + " " + r.getLineNbr());
            holder.towards.setText("mot " + r.getTowardDirection());
            String note = "";
            if (r.getPublicNote() != null) {
                note = r.getPublicNote();
            }
            if (r.getText() != null) {
                if(note.length() < 5){
                    note += r.getText();
                } else {
                    note += "\n" + r.getText();
                }
            }
            holder.noteView.setText(note);
            holder.transportImage.setImageResource(FillUIHelper.getDrawableFromLineType(r.getLineTypeId()));

        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return POSITION_VIEW;
        } else {
            return TRANSPORT_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return Integer.parseInt(journey.getNbrChanges()) * 2 +2; //should be +3
    }
}
