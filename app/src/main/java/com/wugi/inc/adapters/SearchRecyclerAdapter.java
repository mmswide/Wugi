package com.wugi.inc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.activities.EventDetailActivity;
import com.wugi.inc.activities.VenueDetailActivity;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.SearchType;
import com.wugi.inc.models.Type;
import com.wugi.inc.models.Venue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class SearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Event> eventList;
    private List<Venue> venueList;
    private SearchType type;

    public void refresh(ArrayList<Event> eventList, ArrayList<Venue> venueList, SearchType type) {
        this.eventList = eventList;
        this.venueList = venueList;
        this.type = type;
        notifyDataSetChanged();
    }

    public SearchRecyclerAdapter(Context mContext, List<Event> eventList, List<Venue> venueList, SearchType type) {
        this.mContext = mContext;
        this.eventList = eventList;
        this.venueList = venueList;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_row, parent, false);
        return new SearchViewHolder(itemView);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SearchViewHolder){
            if (type == SearchType.EVENT_TYPE) {
                final Event event = eventList.get(position);
                Picasso.with(mContext).load(event.getImageThumbURL()).into(((SearchViewHolder) holder).thumbnail);

                ((SearchViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, EventDetailActivity.class);

                        Gson gson = new Gson();
                        String jsonEventString = gson.toJson(event);
                        intent.putExtra("event", jsonEventString);
                        mContext.startActivity(intent);
                    }
                });
            }
            if (type == SearchType.VENUE_TYPE) {
                final Venue venue = venueList.get(position);
                Picasso.with(mContext).load(venue.getImageThumbURL()).into(((SearchViewHolder) holder).thumbnail);
                ((SearchViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, VenueDetailActivity.class);
                        Gson gson = new Gson();
                        String jsonVenueString = gson.toJson(venue);
                        intent.putExtra("venue", jsonVenueString);
                        mContext.startActivity(intent);
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        if (type == SearchType.EVENT_TYPE) {
            return this.eventList.size();
        }
        if (type == SearchType.VENUE_TYPE) {
            return this.venueList.size();
        }
        return 0;
    }
}
