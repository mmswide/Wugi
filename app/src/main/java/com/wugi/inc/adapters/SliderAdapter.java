package com.wugi.inc.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.models.Photo;

import java.util.ArrayList;

/**
 * Created by storm on 12/19/2017.
 */

public class SliderAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Photo> photoList;
    private LayoutInflater inflater;

    // constructor
    public SliderAdapter(Context context,
                                  ArrayList<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return this.photoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.slider_item, container,
                false);

        photoView = (PhotoView) viewLayout.findViewById(R.id.imgDisplay);

        Photo photo = photoList.get(position);
        Picasso.with(context)
                .load(photo.getFilename())
                .resize((int)photo.getWidth()/4, (int)photo.getHeight()/4)
                .centerCrop()
                .into(photoView);

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
