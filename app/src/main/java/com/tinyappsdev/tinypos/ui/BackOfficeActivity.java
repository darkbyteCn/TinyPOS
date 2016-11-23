package com.tinyappsdev.tinypos.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tinyappsdev.tinypos.AppConst;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseActivity;


public class BackOfficeActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_office);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);

        String serverAddress = mSharedPreferences.getString("serverAddress", "");
        if(!serverAddress.contains(":"))
            serverAddress += ":" + String.valueOf(AppConst.DEFAULT_SERVER_PORT);
        mWebView.loadUrl(String.format(
                "%s://%s/web/",
                mSharedPreferences.getBoolean("serverSecure", false) ? "https" : "http",
                serverAddress
        ));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
