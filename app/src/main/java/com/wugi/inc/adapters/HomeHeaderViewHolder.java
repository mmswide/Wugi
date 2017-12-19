package com.wugi.inc.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wugi.inc.R;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by storm on 12/16/2017.
 */

public class HomeHeaderViewHolder extends RecyclerView.ViewHolder {
    ViewPager viewPager;
    CircleIndicator indicator;

    public HomeHeaderViewHolder(View view) {
        super(view);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        indicator = (CircleIndicator) view.findViewById(R.id.indicator);
    }
}
