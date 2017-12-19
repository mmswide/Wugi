package com.wugi.inc.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.wugi.inc.R;

/**
 * Created by storm on 12/16/2017.
 */

public class BrowseCategoryHeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_name;

    public BrowseCategoryHeaderViewHolder(View view) {
        super(view);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
    }
}
