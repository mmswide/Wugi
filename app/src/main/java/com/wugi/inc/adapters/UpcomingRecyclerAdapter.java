package com.wugi.inc.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.activities.EventDetailActivity;
import com.wugi.inc.fragments.UpcomingFragment;
import com.wugi.inc.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class UpcomingRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Event> eventList;

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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_row, parent, false);
        return new UpcomingViewHolder(itemView);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof UpcomingViewHolder){
            final Event event = eventList.get(position);
            Picasso.with(mContext).load(event.getImageThumbURL()).into(((UpcomingViewHolder) holder).thumbnail);

            ((UpcomingViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }
}
