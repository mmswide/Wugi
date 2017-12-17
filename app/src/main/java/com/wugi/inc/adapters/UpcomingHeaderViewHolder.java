package com.wugi.inc.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.wugi.inc.R;

import org.w3c.dom.Text;

import butterknife.ButterKnife;

/**
 * Created by storm on 12/16/2017.
 */

public class UpcomingHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView tv_first, tv_fristNum;
    TextView tv_second, tv_secondNum;
    TextView tv_third, tv_thirdNum;
    TextView tv_forth, tv_forthNum;
    TextView tv_fifth, tv_fifthNum;
    TextView tv_sixth, tv_sixthNum;
    TextView tv_seventh, tv_seventhNum;

    public UpcomingHeaderViewHolder(View view) {
        super(view);
        tv_first = (TextView) view.findViewById(R.id.tv_first);
        tv_fristNum = (TextView) view.findViewById(R.id.tv_firstNum);
        tv_second = (TextView) view.findViewById(R.id.tv_second);
        tv_secondNum = (TextView) view.findViewById(R.id.tv_secondNum);
        tv_third = (TextView) view.findViewById(R.id.tv_third);
        tv_thirdNum = (TextView) view.findViewById(R.id.tv_thirdNum);
        tv_forth = (TextView) view.findViewById(R.id.tv_fourth);
        tv_forthNum = (TextView) view.findViewById(R.id.tv_fourthNum);
        tv_fifth = (TextView) view.findViewById(R.id.tv_fifth);
        tv_fifthNum = (TextView) view.findViewById(R.id.tv_fifthNum);
        tv_sixth = (TextView) view.findViewById(R.id.tv_sixth);
        tv_sixthNum = (TextView) view.findViewById(R.id.tv_sixthNum);
        tv_seventh = (TextView) view.findViewById(R.id.tv_seventh);
        tv_seventhNum = (TextView) view.findViewById(R.id.tv_seventhNum);
    }
}
