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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class BrowseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<BrowseEvent> browseEventList;

    public void refresh(ArrayList<BrowseEvent> items) {
        this.browseEventList = items;
        notifyDataSetChanged();
    }

    public BrowseRecyclerAdapter(Context mContext, List<BrowseEvent> eventList) {
        this.mContext = mContext;
        this.browseEventList = eventList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_row, parent, false);
        return new UpcomingViewHolder(itemView);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof UpcomingViewHolder){
            BrowseEvent event = browseEventList.get(position);
            Picasso.with(mContext).load(event.getBrowseEventImg()).into(((UpcomingViewHolder) holder).thumbnail);

            ((UpcomingViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.browseEventList.size();
    }
}
