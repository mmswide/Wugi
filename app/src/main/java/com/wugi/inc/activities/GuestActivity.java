package com.wugi.inc.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

import com.wugi.inc.R;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuestActivity extends AppCompatActivity {

    @BindView(R.id.acceptButton) Button acceptButton;
    @BindView(R.id.declineButton) Button declineButton;
    @BindView(R.id.webView) WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        ButterKnife.bind(this);
        webView.loadUrl("file:///android_asset/termsofuse.html");

    }
    @OnClick(R.id.acceptButton)
    public void accept() {
        Intent intent = new Intent(GuestActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.declineButton)
    public void decline() {
        finish();
        overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
    }
}
