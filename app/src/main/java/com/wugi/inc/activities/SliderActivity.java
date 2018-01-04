package com.wugi.inc.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wugi.inc.R;
import com.wugi.inc.adapters.SliderAdapter;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.models.Photo;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    @BindView(R.id.shareButton)
            ImageButton shareButton;

    ArrayList<Photo> photoList = new ArrayList<Photo>();
    private Gallery gallery;
    int position;

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

        this.position = extras.getInt("position");

        tv_title.setText(this.gallery.getVenue().getName());
        tv_gallery_title.setText(this.gallery.getTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String eventDateStr = dateFormat.format(gallery.getEventDate());
        tv_date.setText(eventDateStr);

        viewPager.setAdapter(new SliderAdapter(this, this.photoList));
        viewPager.setCurrentItem(this.position);
    }

    @OnClick(R.id.dismissButton)
    void onDismiss() {
        finish();
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @OnClick(R.id.shareButton)
    void share() {
        Photo photo = this.photoList.get(this.viewPager.getCurrentItem());
        String url = photo.getFilename();
        Picasso.with(getApplicationContext())
                .load(url)
                .resize((int)photo.getWidth()/3, (int)photo.getHeight()/3)
                .into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }
}
