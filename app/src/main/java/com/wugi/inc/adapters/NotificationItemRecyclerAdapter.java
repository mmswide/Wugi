package com.wugi.inc.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.models.Notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class NotificationItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<String> imageList;

    public void refresh(ArrayList<String> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public NotificationItemRecyclerAdapter(Context mContext, List<String> imageList) {
        this.mContext = mContext;
        this.imageList = imageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item_row, parent, false);
        return new NotificationItemViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NotificationItemViewHolder){
            NotificationItemViewHolder viewHolder = (NotificationItemViewHolder) holder;
            String imageURL = imageList.get(position);
            Picasso.with(mContext).load(imageURL).into(viewHolder.thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return this.imageList.size();
    }
}
