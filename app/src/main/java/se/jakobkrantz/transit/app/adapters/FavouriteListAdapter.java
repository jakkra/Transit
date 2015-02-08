package se.jakobkrantz.transit.app.adapters;/*
 * Created by krantz on 14-11-21.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import se.jakobkrantz.transit.app.R;
import se.jakobkrantz.transit.app.database.SimpleJourney;
import se.jakobkrantz.transit.app.viewholders.ViewHolderItem;

import java.util.List;

public class FavouriteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<SimpleJourney> simpleJourneys;
    private List<SimpleJourney> recentSearches;
    private int headerFavourite = 0;
    private int headerRecent = 1;
    private final OnItemChangeListener listener;
    private int nbrItemsShow;


    public FavouriteListAdapter(List<SimpleJourney> simpleJourneys, List<SimpleJourney> recentSearches, OnItemChangeListener listener, int nbrItemsShow) {
        this.simpleJourneys = simpleJourneys;
        this.recentSearches = recentSearches;
        this.listener = listener;
        this.nbrItemsShow = nbrItemsShow;
        updateRecentHeaderPos();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favourite_listview_item, viewGroup, false);
            ViewHolderClickListener l = new ViewHolderClickListener() {
                @Override
                public void onViewClick(View caller, int position) {
                    if (position < headerRecent) {
                        listener.onItemClickListener(simpleJourneys.get(position - 1));

                    } else if (position > headerRecent) {
                        listener.onItemClickListener(recentSearches.get(position - headerRecent - 1));
                    }
                }

                @Override
                public void onViewLongClick(View caller, int position) {
                    if (position < headerRecent) {
                        listener.onItemLongClickListener(simpleJourneys.get(position - 1));
                        //removeFavourite(position);
                    } else if (position > headerRecent) {
                        listener.onItemLongClickListener(recentSearches.get(position - headerRecent - 1));
                    }
                }
            };
            return new ViewHolderItem(v, l);

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_list_item_divider, viewGroup, false);
            return new ViewHolderHeader(v);
        } else {
            Log.e("WRONG TYPE, not supported in recycleView", "Returning null!");
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == headerFavourite || position == headerRecent) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderItem) {
            TextView row = ((ViewHolderItem) viewHolder).favTextView;
            if (i < headerRecent) {
                row.setText(simpleJourneys.get(i - 1).toString());
            } else if (i > headerRecent) {
                row.setText(recentSearches.get(i - headerRecent - 1).toString());
            }
        } else if (viewHolder instanceof ViewHolderHeader) {
            TextView header = ((ViewHolderHeader) viewHolder).header;
            if (i == headerFavourite) {
                header.setText(R.string.fav_header);
            } else if (i == headerRecent) {
                header.setText(R.string.rec_header);
            }
        }
    }

    @Override
    public int getItemCount() {
        return simpleJourneys.size() + recentSearches.size() + 2;
    }


    public void addFavourite(SimpleJourney simpleJourney) {
        simpleJourneys.add(headerFavourite, simpleJourney);
        if (simpleJourneys.size() > nbrItemsShow) {
            listener.onFavouriteItemAdded(simpleJourneys.remove(nbrItemsShow));
            notifyItemRemoved(headerRecent - 1);
        }
        updateRecentHeaderPos();
        notifyItemInserted(headerFavourite + 1);
    }

    public void addRecentJourney(SimpleJourney s) {
        recentSearches.add(0, s);
        if (recentSearches.size() > nbrItemsShow) {
            listener.onRecentSearchItemAdded(recentSearches.remove(nbrItemsShow));
            notifyItemRemoved(getItemCount());
        }
        updateRecentHeaderPos();
        notifyItemInserted(headerRecent + 1);
    }

    public void removeFavourite(int position) {
        SimpleJourney s = simpleJourneys.remove(position - 1);
        notifyItemRemoved(position);
        updateRecentHeaderPos();
        listener.onFavouriteItemRemoved(s);
    }

    private void updateRecentHeaderPos() {
        headerRecent = simpleJourneys.size() + 1;
    }

    public void clearAllSearches() {
        int deleted = recentSearches.size();
        recentSearches.clear();
        notifyItemRangeRemoved(headerRecent + 1, deleted);
    }

    public void clearAllFavourites() {
        int deleted = simpleJourneys.size();
        simpleJourneys.clear();
        notifyItemRangeRemoved(headerFavourite + 1, deleted);
        updateRecentHeaderPos();
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public final TextView header;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            header = (TextView) itemView.findViewById(R.id.fav_list_header);

        }
    }

    public static interface OnItemChangeListener {
        public void onRecentSearchItemAdded(SimpleJourney s);

        public void onItemClickListener(SimpleJourney s);

        public void onItemLongClickListener(SimpleJourney s);

        public void onFavouriteItemAdded(SimpleJourney s);

        public void onFavouriteItemRemoved(SimpleJourney s);


    }
}


