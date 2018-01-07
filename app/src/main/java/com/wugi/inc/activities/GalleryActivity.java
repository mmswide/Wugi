package com.wugi.inc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wugi.inc.R;
import com.wugi.inc.adapters.GalleryRecyclerAdapter;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.views.MarginDecoration;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private ArrayList<Gallery> galleryList = new ArrayList<Gallery>();
    private GalleryRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Bundle extras = getIntent().getExtras();
        String jsonGalleryListString = extras.getString("galleryList");
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<Gallery>>(){}.getType();
        ArrayList<Gallery> galleryList = new Gson().fromJson(jsonGalleryListString, type);
        this.galleryList = galleryList;

        adapter = new GalleryRecyclerAdapter(this, galleryList);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, (int) Utils.convertDpToPixel(10), true));
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isPositionHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
