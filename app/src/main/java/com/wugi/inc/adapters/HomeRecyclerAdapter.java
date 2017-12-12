package com.wugi.inc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/11/2017.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder> {
    private Context mContext;
    private List<Event> eventList;

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public HomeViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    public void refresh(ArrayList<Event> items) {
        this.eventList = items;
        notifyDataSetChanged();
    }

    public HomeRecyclerAdapter(Context mContext, List<Event> eventList) {
        this.mContext = mContext;
        this.eventList = eventList;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_row, parent, false);
        return new HomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        Event event = eventList.get(position);
        Picasso.with(mContext).load(event.getImageThumbURL()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }
}
