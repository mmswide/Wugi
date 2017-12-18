package com.wugi.inc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;
import com.wugi.inc.R;
import com.wugi.inc.activities.EventDetailActivity;
import com.wugi.inc.models.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by storm on 12/11/2017.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Event> eventList;
    private List<Event> featuredEventList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    HomePagerAdapter pagerAdapter;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    public void refresh(ArrayList<Event> items, ArrayList<Event> featuredEvents) {
        this.eventList = items;
        this.featuredEventList = featuredEvents;
        notifyDataSetChanged();
    }

    public HomeRecyclerAdapter(Context mContext, List<Event> eventList, List<Event> featuredEventList) {
        this.mContext = mContext;
        this.eventList = eventList;
        this.featuredEventList = featuredEventList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_header_layout, parent, false);
            return new HomeHeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_row, parent, false);
            return new HomeViewHolder(itemView);
        }
        throw new RuntimeException("No match for " + viewType + ".");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HomeHeaderViewHolder){
            HomeHeaderViewHolder headerViewHolder = (HomeHeaderViewHolder) holder;
            final ViewPager viewPager = headerViewHolder.viewPager;
            pagerAdapter = new HomePagerAdapter(mContext, featuredEventList);
            viewPager.setAdapter(pagerAdapter);

            CirclePageIndicator indicator = headerViewHolder.indicator;

            indicator.setViewPager(viewPager);

            final float density = mContext.getResources().getDisplayMetrics().density;

            //Set circle indicator radius
            indicator.setRadius(5 * density);

            NUM_PAGES =featuredEventList.size();

            // Auto start of viewpager
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };
            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 3000, 3000);

            // Pager listener over indicator
            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;

                }

                @Override
                public void onPageScrolled(int pos, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int pos) {

                }
            });

        }else if(holder instanceof HomeViewHolder){
            final Event event = eventList.get(position - 1);
            Picasso.with(mContext).load(event.getImageThumbURL()).into(((HomeViewHolder) holder).thumbnail);

            ((HomeViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
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
