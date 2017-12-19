package com.wugi.inc.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.activities.EventDetailActivity;
import com.wugi.inc.activities.VenueDetailActivity;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.BrowseVenueType;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.models.Type;
import com.wugi.inc.models.Venue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/17/2017.
 */

public class BrowseCategoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Event> eventList;
    private List<Venue> venueList;
    private Type type;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private BrowseEvent browseEvent;
    private BrowseEvent browseVenue;
    private BrowseVenueType venueType;

    public void refresh(ArrayList<Event> eventList, ArrayList<Venue> venueList, Type type) {
        this.eventList = eventList;
        this.venueList = venueList;
        this.type = type;
        notifyDataSetChanged();
    }

    public BrowseCategoryRecyclerAdapter(Context mContext, List<Event> eventList, Type type) {
        this.mContext = mContext;
        this.eventList = eventList;
        this.type = type;
    }

    public void setHeader(BrowseEvent browseEvent, BrowseEvent browseVenue, BrowseVenueType venueType) {
        this.browseEvent = browseEvent;
        this.browseVenue = browseVenue;
        this.venueType = venueType;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_category_header_layout, parent, false);
            return new BrowseCategoryHeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.browse_category_row, parent, false);
            return new BrowseCategoryViewHolder(itemView);
        }
        throw new RuntimeException("No match for " + viewType + ".");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof BrowseCategoryHeaderViewHolder){
            BrowseCategoryHeaderViewHolder headerViewHolder = (BrowseCategoryHeaderViewHolder) holder;
            switch (this.type) {
                case EVENT_TYPE:
                    headerViewHolder.tv_name.setText(this.browseEvent.getEventName() + " Events");
                    break;
                case VENUE_TYPE:
                    break;
                case TYPE_TYPE:
                    headerViewHolder.tv_name.setText(this.venueType.getVenueTypeName() + " Venues");
                    break;
            }
        }else if(holder instanceof BrowseCategoryViewHolder){

            switch (this.type) {
                case EVENT_TYPE:
                    final Event event = eventList.get(position - 1);
                    Picasso.with(mContext).load(event.getImageThumbURL()).into(((BrowseCategoryViewHolder) holder).thumbnail);
                    ((BrowseCategoryViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, EventDetailActivity.class);

                            Gson gson = new Gson();
                            String jsonEventString = gson.toJson(event);
                            intent.putExtra("event", jsonEventString);
                            mContext.startActivity(intent);
                        }
                    });

                    break;
                case VENUE_TYPE:
                    break;
                case TYPE_TYPE:
                    final Venue venue = venueList.get(position - 1);
                    Picasso.with(mContext).load(venue.getImageThumbURL()).into(((BrowseCategoryViewHolder) holder).thumbnail);
                    ((BrowseCategoryViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, VenueDetailActivity.class);
                            Gson gson = new Gson();
                            String jsonVenueString = gson.toJson(venue);
                            intent.putExtra("venue", jsonVenueString);
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        switch (this.type) {
            case EVENT_TYPE:
                return this.eventList.size() + 1;
            case VENUE_TYPE:
                return 0;
            case TYPE_TYPE:
                return this.venueList.size() + 1;
            default:
                return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }
    public boolean isPositionHeader(int position) {
        return position == 0;
    }
}
