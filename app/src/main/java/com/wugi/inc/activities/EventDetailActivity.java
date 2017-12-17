package com.wugi.inc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wugi.inc.R;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_event)        ImageView iv_event;
    @BindView(R.id.iv_venue)        ImageView iv_venue;
    @BindView(R.id.tv_event_name)
    TextView tv_event_name;
    @BindView(R.id.tv_event_day)
    TextView tv_event_day;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.ll_call)
    LinearLayout ll_call;
    @BindView(R.id.ll_site)
    LinearLayout ll_site;
    @BindView(R.id.ll_map)
    LinearLayout ll_map;
    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.tv_theme)
    TextView tv_theme;
    @BindView(R.id.tv_dress_code)
    TextView tv_dress_code;
    @BindView(R.id.tv_age)
    TextView tv_age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        ButterKnife.bind(this);
    }
}
