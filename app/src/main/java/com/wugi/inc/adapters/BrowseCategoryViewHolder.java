package com.wugi.inc.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wugi.inc.R;

/**
 * Created by storm on 12/16/2017.
 */

public class BrowseCategoryViewHolder extends RecyclerView.ViewHolder {
    public CardView card_view;
    public ImageView thumbnail;

    public BrowseCategoryViewHolder(View view) {
        super(view);
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        card_view = (CardView) view.findViewById(R.id.card_view);
    }
}