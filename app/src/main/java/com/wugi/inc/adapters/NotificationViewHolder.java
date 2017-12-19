package com.wugi.inc.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wugi.inc.R;

/**
 * Created by storm on 12/16/2017.
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_title;
    public TextView tv_date;
    public TextView tv_description;
    public RecyclerView recyclerView;

    public NotificationViewHolder(View view) {
        super(view);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_description = (TextView) view.findViewById(R.id.tv_description);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }
}