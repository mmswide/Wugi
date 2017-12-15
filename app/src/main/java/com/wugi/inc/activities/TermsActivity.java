package com.wugi.inc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.wugi.inc.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsActivity extends AppCompatActivity {
    @BindView(R.id.dismissButton)
    ImageButton dismissButton;
    @BindView(R.id.webView)
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);

        webView.loadUrl("file:///android_asset/termsofuse.html");
    }

    @OnClick(R.id.dismissButton)
    void dismiss() {
        finish();
    }
}
