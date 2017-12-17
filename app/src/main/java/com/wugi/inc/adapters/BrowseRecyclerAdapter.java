package com.wugi.inc.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
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
    private List<BrowseVenueType> browseVenueTypeList;
    private Type type;

    public void refresh(ArrayList<BrowseEvent> browseEventList, ArrayList<BrowseVenueType> browseVenueTypeList, Type type) {
        this.browseEventList = browseEventList;
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
                    BrowseEvent event = browseEventList.get(position);
                    ((BrowseViewHolder) holder).tv_title.setText(event.getEventName());
                    Picasso.with(mContext).load(event.getBrowseEventImg()).into(((BrowseViewHolder) holder).thumbnail);
                    ((BrowseViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    break;
                case VENUE_TYPE:
                    break;
                case TYPE_TYPE:
                    BrowseVenueType browseVenueType = browseVenueTypeList.get(position);
                    ((BrowseViewHolder) holder).tv_title.setText(browseVenueType.getVenueTypeName());
                    Picasso.with(mContext).load(browseVenueType.getVenueTypeThumImg()).into(((BrowseViewHolder) holder).thumbnail);
                    ((BrowseViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

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
                return 0;
            case TYPE_TYPE:
                return this.browseVenueTypeList.size();
            default:
                return 0;
        }
    }
}
