package com.wugi.inc.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wugi.inc.R;
import com.wugi.inc.utils.Utils;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsActivity extends AppCompatActivity {
    @BindView(R.id.dismissButton)
    ImageButton dismissButton;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_logo)
    ImageView iv_logo;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle extras = getIntent().getExtras();

        webView.setWebViewClient(new WebViewClient());
        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        progressDialog = Utils.createProgressDialog(this);

        if (extras.getBoolean("accept")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            tv_title.setText("TERMS AND CONDITIONS");
            tv_title.setVisibility(View.GONE);
            iv_logo.setVisibility(View.VISIBLE);
            webView.loadUrl("file:///android_asset/termsofuse.html");
        } else if (extras.getBoolean("terms")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            tv_title.setText("TERMS AND CONDITIONS");
            tv_title.setVisibility(View.GONE);
            iv_logo.setVisibility(View.VISIBLE);

            dismissButton.setVisibility(View.GONE);

            webView.loadUrl("file:///android_asset/termsofuse.html");
        } else if (extras.getBoolean("about")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            tv_title.setText("ABOUT WUGI");
            tv_title.setVisibility(View.VISIBLE);
            iv_logo.setVisibility(View.GONE);

            dismissButton.setVisibility(View.GONE);

            webView.loadUrl("https://s3-us-west-2.amazonaws.com/wugi/about.html");
        } else if (extras.getBoolean("privacy")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            tv_title.setText("PRIVACY POLICY");
            tv_title.setVisibility(View.VISIBLE);
            iv_logo.setVisibility(View.GONE);

            dismissButton.setVisibility(View.GONE);

            webView.loadUrl("https://s3-us-west-2.amazonaws.com/wugi/privacy.html");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.dismissButton)
    void dismiss() {
        finish();
    }

    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub

            super.onPageFinished(view, url);
            progressDialog.dismiss();

        }

    }
}
