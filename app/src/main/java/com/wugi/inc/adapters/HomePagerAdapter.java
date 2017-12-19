package com.wugi.inc.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.activities.EventDetailActivity;
import com.wugi.inc.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class HomePagerAdapter extends PagerAdapter {

    Context context;
    private List<Event> eventList;
    LayoutInflater layoutInflater;


    public HomePagerAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.home_header_item, container, false);

        final Event event = this.eventList.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Picasso.with(context).load(event.getImageFeatureURL()).into(imageView);

        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);

                Gson gson = new Gson();
                String jsonEventString = gson.toJson(event);
                intent.putExtra("event", jsonEventString);
                context.startActivity(intent);
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
