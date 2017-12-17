package com.wugi.inc.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.wugi.inc.R;

/**
 * Created by storm on 12/17/2017.
 */

public class GalleryViewHolder extends RecyclerView.ViewHolder {
    public ImageView thumbnail;

    public GalleryViewHolder(View view) {
        super(view);
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
    }
}
