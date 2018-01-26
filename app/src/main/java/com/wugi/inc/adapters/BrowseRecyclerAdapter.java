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
import com.wugi.inc.activities.BrowseCategoryActivity;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.BrowseVenueType;
import com.wugi.inc.models.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class BrowseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<BrowseEvent> browseEventList;
    private List<BrowseEvent> browseVenueList;
    private List<BrowseVenueType> browseVenueTypeList;
    private Type type;

    public void refresh(ArrayList<BrowseEvent> browseEventList, ArrayList<BrowseEvent> browseVenueList, ArrayList<BrowseVenueType> browseVenueTypeList, Type type) {
        this.browseEventList = browseEventList;
        this.browseVenueList = browseVenueList;
        this.browseVenueTypeList = browseVenueTypeList;
        this.type = type;
        notifyDataSetChanged();
    }

    public BrowseRecyclerAdapter(Context mContext, List<BrowseEvent> eventList, Type type) {
        this.mContext = mContext;
        this.browseEventList = eventList;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browse_row, parent, false);
        return new BrowseViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BrowseViewHolder){
            switch (this.type) {
                case EVENT_TYPE:
                    final BrowseEvent event = browseEventList.get(position);
                    ((BrowseViewHolder) holder).tv_title.setText(event.getEventName());
                    Picasso.with(mContext).load(event.getBrowseEventImg()).into(((BrowseViewHolder) holder).thumbnail);
                    ((BrowseViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, BrowseCategoryActivity.class);
                            intent.putExtra("type", type.getTypeCode());
                            Gson gson = new Gson();
                            String jsonEventString = gson.toJson(event);
                            intent.putExtra("event_type", jsonEventString);
                            mContext.startActivity(intent);
                        }
                    });

                    break;
                case VENUE_TYPE:
                    final BrowseEvent venue = browseVenueList.get(position);
                    ((BrowseViewHolder) holder).tv_title.setText(venue.getEventName());
                    Picasso.with(mContext).load(venue.getBrowseEventImg()).into(((BrowseViewHolder) holder).thumbnail);
                    ((BrowseViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, BrowseCategoryActivity.class);
                            intent.putExtra("type", type.getTypeCode());
                            Gson gson = new Gson();
                            String jsonEventString = gson.toJson(venue);
                            intent.putExtra("event_type", jsonEventString);
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case TYPE_TYPE:
                    final BrowseVenueType browseVenueType = browseVenueTypeList.get(position);
                    ((BrowseViewHolder) holder).tv_title.setText(browseVenueType.getVenueTypeName());
                    Picasso.with(mContext).load(browseVenueType.getVenueTypeThumImg()).into(((BrowseViewHolder) holder).thumbnail);
                    ((BrowseViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, BrowseCategoryActivity.class);
                            intent.putExtra("type", type.getTypeCode());
                            Gson gson = new Gson();
                            String jsonEventString = gson.toJson(browseVenueType);
                            intent.putExtra("event_type", jsonEventString);
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
                return this.browseEventList.size();
            case VENUE_TYPE:
                return this.browseVenueList.size();
            case TYPE_TYPE:
                return this.browseVenueTypeList.size();
            default:
                return 0;
        }
    }
}
