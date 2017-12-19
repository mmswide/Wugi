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
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by storm on 12/16/2017.
 */

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Notification> notificationList;
    private NotificationItemRecyclerAdapter recyclerAdapter;

    public void refresh(ArrayList<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    public NotificationRecyclerAdapter(Context mContext, List<Notification> notificationList) {
        this.mContext = mContext;
        this.notificationList = notificationList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row, parent, false);
        return new NotificationViewHolder(itemView);

    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        if (elapsedDays > 0)
            return String.valueOf(elapsedDays) + " days";

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        if (elapsedHours > 0)
            return String.valueOf(elapsedHours) + " hours";

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        if (elapsedMinutes > 0)
            return String.valueOf(elapsedMinutes) + " minutes";

        long elapsedSeconds = different / secondsInMilli;

        return String.valueOf(elapsedSeconds) + " seconds";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NotificationViewHolder){
            NotificationViewHolder viewHolder = (NotificationViewHolder) holder;
            Notification notification = notificationList.get(position);
            viewHolder.tv_title.setText(notification.getTitle());

            String timeDiff = this.printDifference(notification.getCreatedAt(), new Date());
            viewHolder.tv_date.setText(timeDiff);
            viewHolder.tv_description.setText(notification.getDescription());

            if (notification.getImages() != null && notification.getImages().size() > 0) {
                this.recyclerAdapter = new NotificationItemRecyclerAdapter(this.mContext, notification.getImages());

                final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                viewHolder.recyclerView.setLayoutManager(layoutManager);

                viewHolder.recyclerView.setAdapter(this.recyclerAdapter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.notificationList.size();
    }
}
