package com.wugi.inc.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.wugi.inc.R;

/**
 * Created by storm on 12/16/2017.
 */

public class HomeViewHolder extends RecyclerView.ViewHolder {
    public ImageView thumbnail;

    public HomeViewHolder(View view) {
        super(view);
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
    }
}