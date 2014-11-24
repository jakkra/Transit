package se.jakobkrantz.transit.app.adapters;/*
 * Created by krantz on 14-11-22.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.skanetrafikenAPI.Journey;
import se.jakobkrantz.transit.app.skanetrafikenAPI.RouteLink;
import se.jakobkrantz.transit.app.skanetrafikenAPI.TimeAndDateConverter;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int LOAD_MORE_HOLDER = 0;
    private static final int ITEM_HOLDER = 1;
    private ArrayList<Journey> journeys;
    private OnResultItemClicked listener;


    public ResultListAdapter(ArrayList<Journey> journeys, OnResultItemClicked listener) {
        this.journeys = journeys;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ViewHolderClickListener l = new ViewHolderClickListener() {
            @Override
            public void onViewClick(View caller, int position) {
                if (position == 0) {
                    listener.onLoadMoreResults(true);
                } else if (position == journeys.size() + 1) {
                    listener.onLoadMoreResults(false);
                } else {
                    listener.onResultClicked(journeys.get(position - 1));
                }
            }

            @Override
            public void onViewLongClick(View caller, int position) {

            }
        };
        if (viewType == ITEM_HOLDER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_list_item, viewGroup, false);
            return new ViewHolderResult(v, l);

        } else if (viewType == LOAD_MORE_HOLDER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_load_more_header, viewGroup, false);
            return new ViewHolderLoadHeader(v, l);


        } else {
            Log.e("WRONG TYPE, not supported in recycleView", "Returning null!");
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderResult) {
            ViewHolderResult result = ((ViewHolderResult) viewHolder);
            Journey j = journeys.get(i - 1);
            List<RouteLink> links = journeys.get(i - 1).getRouteLinks();
            String route = "";
            String mess = "";
            for (RouteLink r : links) {
                route += r.getLineName() + " " + r.getLineNbr() + " -> ";
                mess += r.getSummary() + "\n";
            }
            result.timeBetween.setText(j.getDepDateTime() + " - " + j.getArrDateTime());
            result.timeToArrival.setText(j.getStartStation() + " om" + TimeAndDateConverter.timeToDeparture(j.getArrDateTime()));
            result.timeBetween.setText(TimeAndDateConverter.formatTime(j.getDepDateTime()) + " - " + TimeAndDateConverter.formatTime(j.getArrDateTime()));
            result.nbrDepTime.setText("Tid " + TimeAndDateConverter.getTravelTimeinMinutes(j.getDepDateTime(), j.getArrDateTime()));

        } else if (viewHolder instanceof ViewHolderLoadHeader) {
            TextView header = ((ViewHolderLoadHeader) viewHolder).loadMore;
            if (i == 0) {
                header.setText(R.string.load_earlier);
            } else if (i == journeys.size() + 1) {
                header.setText(R.string.load_later);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount() - 1) {
            return LOAD_MORE_HOLDER;
        } else {
            return ITEM_HOLDER;
        }

    }

    @Override
    public int getItemCount() {
        return journeys.size() + 2;
    }


    public void addJourneys(ArrayList<Journey> journeys) {

    }

    public static interface OnResultItemClicked {
        public void onResultClicked(Journey j);

        public void onResultLongClickListener(Journey j);

        /**
         * @param b true if load earlier result, false if load later results
         */
        public void onLoadMoreResults(boolean b);
    }


}
