package com.jomhornews.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
    private static final String HOME = "https://www.jomhornews.com";
    private WebView web;
    private ProgressBar bar;

    private static final String OFFLINE =
        "<html dir='rtl'><head><meta name='viewport' content='width=device-width,initial-scale=1'></head>"
        + "<body style='font-family:sans-serif;text-align:center;padding:90px 24px;color:#333'>"
        + "<h2 style='color:#3952A3'>خبرگزاری جمهور</h2>"
        + "<p>اتصال به اینترنت برقرار نیست.</p>"
        + "<button onclick=\"location.href='" + HOME + "'\" style='background:#F7941D;color:#fff;border:0;"
        + "border-radius:24px;padding:14px 36px;font-size:18px'>تلاش دوباره</button></body></html>";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        FrameLayout root = new FrameLayout(this);
        web = new WebView(this);
        bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.getProgressDrawable().setColorFilter(Color.parseColor("#F7941D"), android.graphics.PorterDuff.Mode.SRC_IN);
        root.addView(web, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.addView(bar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 8));
        setContentView(root);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setMediaPlaybackRequiresUserGesture(false);

        web.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView v, int p) {
                bar.setProgress(p);
                bar.setVisibility(p >= 100 ? View.GONE : View.VISIBLE);
            }
        });
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                String host = u.getHost();
                if (host != null && host.endsWith("jomhornews.com")) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) {}
                return true;
            }
            @Override public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame()) v.loadDataWithBaseURL(HOME, OFFLINE, "text/html", "utf-8", null);
            }
        });
        if (b == null) web.loadUrl(HOME); else web.restoreState(b);
    }

    @Override protected void onSaveInstanceState(Bundle o) { super.onSaveInstanceState(o); web.saveState(o); }

    @Override public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }
}
