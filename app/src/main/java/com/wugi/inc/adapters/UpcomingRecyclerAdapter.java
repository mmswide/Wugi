package com.wugi.inc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class UpcomingRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Event> eventList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public void refresh(ArrayList<Event> items) {
        this.eventList = items;
        notifyDataSetChanged();
    }

    public UpcomingRecyclerAdapter(Context mContext, List<Event> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_header_layout, parent, false);
            return new UpcomingHeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.upcoming_row, parent, false);
            return new UpcomingViewHolder(itemView);
        }
        throw new RuntimeException("No match for " + viewType + ".");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Event event = eventList.get(position);
        if(holder instanceof UpcomingHeaderViewHolder){
            UpcomingHeaderViewHolder headerViewHolder = (UpcomingHeaderViewHolder) holder;

        }else if(holder instanceof UpcomingViewHolder){
            Picasso.with(mContext).load(event.getImageThumbURL()).into(((UpcomingViewHolder) holder).thumbnail);

            ((UpcomingViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
    private Event getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
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
