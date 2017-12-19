package com.wugi.inc.activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wugi.inc.R;
import com.wugi.inc.adapters.SliderAdapter;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.models.Photo;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SliderActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.dismissButton)
    ImageButton dismissButton;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_gallery_title)
    TextView tv_gallery_title;
    @BindView(R.id.tv_date)
    TextView tv_date;

    ArrayList<Photo> photoList = new ArrayList<Photo>();
    private Gallery gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        String galleryString = extras.getString("gallery");
        Gson gson = new Gson();
        this.gallery = gson.fromJson(galleryString, Gallery.class);

        String photoListString = extras.getString("photoList");

        @SuppressWarnings("serial")
        Type collectionType = new TypeToken<ArrayList<Photo>>() {
        }.getType();
        this.photoList = gson.fromJson(photoListString, collectionType);

        tv_title.setText(this.gallery.getVenue().getName());
        tv_gallery_title.setText(this.gallery.getTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        String eventDateStr = dateFormat.format(gallery.getEventDate());
        tv_date.setText(eventDateStr);

        viewPager.setAdapter(new SliderAdapter(this, this.photoList));
    }

    @OnClick(R.id.dismissButton)
    void onDismiss() {
        finish();
    }
}
