package com.wugi.inc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.activities.PhotoActivity;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.models.Notification;

import java.util.ArrayList;
import java.util.Calendar;
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

    public static String getDateDifferenceInDDMMYYYY(Date from, Date to) {
        Calendar fromDate= Calendar.getInstance();
        Calendar toDate=Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year,month,day;
        System.out.println(fromDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment =fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        System.out.println("increment"+increment);
// DAY CALCULATION
        if (increment != 0) {
            day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
            increment = 1;
        } else {
            day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
        }

// MONTH CALCULATION
        if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
            month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 0;
        }

// YEAR CALCULATION
        year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);
        return   year+"\tYears\t\t"+month+"\tMonths\t\t"+day+"\tDays";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof NotificationViewHolder){
            NotificationViewHolder viewHolder = (NotificationViewHolder) holder;
            final Notification notification = notificationList.get(position);
            viewHolder.tv_title.setText(notification.getTitle());

//            String timeDiff = this.printDifference(notification.getCreatedAt(), new Date());
            long timeInMillis = notification.getCreatedAt().getTime();
            String timeDiff = TimeAgo.using(timeInMillis);
            viewHolder.tv_date.setText(timeDiff);
            viewHolder.tv_description.setText(notification.getDescription());

            if (notification.getImages() != null && notification.getImages().size() > 0) {
                this.recyclerAdapter = new NotificationItemRecyclerAdapter(this.mContext, notification.getImages());

                final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                viewHolder.recyclerView.setLayoutManager(layoutManager);

                viewHolder.recyclerView.setAdapter(this.recyclerAdapter);
            }

            if (notification.getGallery() != null) {
                viewHolder.notification_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Gallery gallery = notification.getGallery();
                        Intent intent = new Intent(mContext, PhotoActivity.class);
                        Gson gson = new Gson();
                        String jsonGalleryString = gson.toJson(gallery);
                        intent.putExtra("gallery", jsonGalleryString);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.notificationList.size();
    }
}
