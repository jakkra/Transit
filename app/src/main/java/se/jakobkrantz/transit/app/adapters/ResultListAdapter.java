package se.jakobkrantz.transit.app.adapters;
/*
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
    public static final int NBR_ITEMS_PER_LOAD = 6;

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
                    listener.onLoadMoreResults(true, journeys.get(0).getDepDateTime());
                } else if (position == journeys.size() + 1) {
                    listener.onLoadMoreResults(false, journeys.get(journeys.size() - 1).getDepDateTime());
                } else {
                    listener.onResultClicked(journeys.get(position - 1));
                }
            }

            @Override
            public void onViewLongClick(View caller, int position) {
                listener.onResultLongClickListener(journeys.get(position - 1));
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
            boolean inTime = false;
            //TODO move those methods and calculations inside Journey or helper class
            for (RouteLink r : links) {
                route += r.getLineName() + " " + r.getLineNbr() + " -> ";
                mess += r.getSummary() + "\n";
                if (r.getDepTimeDeviation() != null) if (r.getDepTimeDeviation().equals("0")) inTime = true;
            }
            result.timeBetween.setText(j.getDepDateTime() + " - " + j.getArrDateTime());
            String travelTime = TimeAndDateConverter.timeToDeparture(j.getArrDateTime());
            String depIn = "";
            if (travelTime.contains("-")) {
                depIn = "Passerat";
            } else {
                depIn = "Avgår från " + j.getStartStation() + " om " + TimeAndDateConverter.timeToDeparture(j.getDepDateTime()) + " min ";
            }

            result.timeToArrival.setText(depIn);
            result.timeBetween.setText(TimeAndDateConverter.formatTime(j.getDepDateTime()) + " - " + TimeAndDateConverter.formatTime(j.getArrDateTime()));
            String arrInTime = "";
            if (inTime) arrInTime = "I tid";
            result.nbrDepTime.setText(TimeAndDateConverter.getTravelTimeinMinutes(j.getDepDateTime(), j.getArrDateTime()) + " min ");

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


    /**
     * @param journeys journeys to bew showed in the list
     * @param b        true if added to beginning, false if added to the end
     */
    public void addJourneys(ArrayList<Journey> update, boolean b) {
        int prevLastItem = journeys.size();
        if (b) {
            journeys.addAll(0, update);
            notifyItemRangeInserted(1, update.size());

        } else {
            journeys.addAll(update);
            notifyItemRangeInserted(prevLastItem + 1, update.size());
        }
    }

    public void setJourneys(ArrayList<Journey> j) {
        if (journeys.size() == NBR_ITEMS_PER_LOAD) {
            journeys = j;
            notifyItemRangeChanged(1, NBR_ITEMS_PER_LOAD);
        } else {
            journeys = j;
            notifyDataSetChanged();
        }

    }


    public static interface OnResultItemClicked {
        public void onResultClicked(Journey j);

        public void onResultLongClickListener(Journey j);

        /**
         * @param b true if load earlier result, false if load later results
         */
        public void onLoadMoreResults(boolean b, String dateAndTime);
    }


}
