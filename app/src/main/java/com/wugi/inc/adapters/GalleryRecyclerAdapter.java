package com.wugi.inc.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.models.Gallery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/17/2017.
 */

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Gallery> galleryList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public void refresh(ArrayList<Gallery> galleryList) {
        this.galleryList = galleryList;
        notifyDataSetChanged();
    }

    public GalleryRecyclerAdapter(Context mContext, List<Gallery> galleryList) {
        this.mContext = mContext;
        this.galleryList = galleryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_header_layout, parent, false);
            return new GalleryHeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gallery_row, parent, false);
            return new GalleryViewHolder(itemView);
        }
        throw new RuntimeException("No match for " + viewType + ".");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof GalleryHeaderViewHolder){
            GalleryHeaderViewHolder headerViewHolder = (GalleryHeaderViewHolder) holder;

        }else if(holder instanceof GalleryViewHolder){
            Gallery gallery = galleryList.get(position - 1);
            Picasso.with(mContext).load(gallery.getCover()).into(((GalleryViewHolder) holder).thumbnail);

            ((GalleryViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.galleryList.size() + 1;
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
