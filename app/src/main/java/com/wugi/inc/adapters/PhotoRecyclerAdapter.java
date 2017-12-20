package com.wugi.inc.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.activities.SliderActivity;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.models.Photo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by storm on 12/17/2017.
 */

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Photo> photoList;
    private Gallery gallery;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public void refresh(ArrayList<Photo> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    public PhotoRecyclerAdapter(Context mContext, ArrayList<Photo> photoList, Gallery gallery) {
        this.mContext = mContext;
        this.photoList = photoList;
        this.gallery = gallery;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_header_layout, parent, false);
            return new PhotoHeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_row, parent, false);
            return new PhotoViewHolder(itemView);
        }
        throw new RuntimeException("No match for " + viewType + ".");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof PhotoHeaderViewHolder){
            PhotoHeaderViewHolder headerViewHolder = (PhotoHeaderViewHolder) holder;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
            String eventDateStr = dateFormat.format(gallery.getEventDate());
            headerViewHolder.tv_name.setText(gallery.getTitle());
            headerViewHolder.tv_date.setText(eventDateStr);

        }else if(holder instanceof PhotoViewHolder){
            Photo photo = photoList.get(position - 1);
//            Picasso.with(mContext).load("https://s3-us-west-2.amazonaws.com/wugi/2017/fri-flyers/thumb/OpiumFridaysDelete12%3A2thumb.jpg").into(((PhotoViewHolder) holder).thumbnail);
            Picasso.with(mContext).load(photo.getFilename())
                    .resize((int)photo.getWidth()/4, (int)photo.getHeight()/4)
                    .centerCrop()
                    .into(((PhotoViewHolder) holder).thumbnail);

            ((PhotoViewHolder) holder).thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SliderActivity.class);

                    Gson gson = new Gson();
                    String galleryString = gson.toJson(gallery);
                    String photoListString = gson.toJson(photoList);
                    intent.putExtra("position", position-1);
                    intent.putExtra("gallery", galleryString);
                    intent.putExtra("photoList", photoListString);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.photoList.size() + 1;
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
