package com.wugi.inc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wugi.inc.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class VenueDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_venue)        CircleImageView iv_venue;
    @BindView(R.id.tv_venue_name)   TextView tv_venue_name;
    @BindView(R.id.tv_venue_day)    TextView tv_venue_day;
    @BindView(R.id.tv_address)      TextView tv_address;
    @BindView(R.id.tv_distance)     TextView tv_distance;
    @BindView(R.id.ll_call)         LinearLayout ll_call;
    @BindView(R.id.ll_site)         LinearLayout ll_site;
    @BindView(R.id.ll_map)          LinearLayout ll_map;
    @BindView(R.id.tv_venue_type)  TextView tv_venue_type;
    @BindView(R.id.tv_neighbor)        TextView tv_neighbor;
    @BindView(R.id.tv_parking)   TextView tv_parking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);

        ButterKnife.bind(this);
    }
}
