package com.wugi.inc.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;
import com.wugi.inc.R;
import com.wugi.inc.models.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by storm on 12/17/2017.
 */

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Event> eventList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public void refresh(ArrayList<Event> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    public PhotoRecyclerAdapter(Context mContext, List<Event> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_header_layout, parent, false);
            return new PhotoHeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_row, parent, false);
            return new PhotoViewHolder(itemView);
        }
        throw new RuntimeException("No match for " + viewType + ".");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof PhotoHeaderViewHolder){
            PhotoHeaderViewHolder headerViewHolder = (PhotoHeaderViewHolder) holder;

        }else if(holder instanceof PhotoViewHolder){
            Event event = eventList.get(position - 1);
            Picasso.with(mContext).load(event.getImageThumbURL()).into(((PhotoViewHolder) holder).thumbnail);

            ((PhotoViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.eventList.size() + 1;
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
