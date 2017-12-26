package com.geotdb.compile.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.geotdb.compile.R;
import com.geotdb.compile.activity.base.BaseAppCompatActivity;

/**
 * Created by Administrator on 2017/5/12.
 */
public class HelpActivtiy extends BaseAppCompatActivity {
    //    private TextView help_tv;
    private String action;

    private WebView help_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_help);
        initView();
        initData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Help");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        help_tv = (TextView) findViewById(R.id.help_tv);
        help_webView = (WebView) findViewById(R.id.help_webView);
    }

    private void initData() {
        action = getIntent().getAction();
        if ("RecordListActivity".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_list.html");
        }
        if ("回次".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_edit_hc.html");
        }
        if ("岩土".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_edit_yt.html");
        }
        if ("取土".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_edit_qt.html");
        }
        if ("取水".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_edit_qs.html");
        }
        if ("动探".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_edit_dt.html");
        }
        if ("标贯".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_edit_bg.html");
        }
        if ("水位".equals(action)) {
            help_webView.loadUrl("file:///android_asset/record_edit_sw.html");
        }
        if ("MainActivity".equals(action)) {
            help_webView.loadUrl("file:///android_asset/help.html");
        }
        help_webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = help_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
