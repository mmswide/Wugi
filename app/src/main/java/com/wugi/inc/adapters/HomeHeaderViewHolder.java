package com.wugi.inc.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.wugi.inc.R;

/**
 * Created by storm on 12/16/2017.
 */

public class HomeHeaderViewHolder extends RecyclerView.ViewHolder {
    ViewPager viewPager;
    CirclePageIndicator indicator;

    public HomeHeaderViewHolder(View view) {
        super(view);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
    }
}
