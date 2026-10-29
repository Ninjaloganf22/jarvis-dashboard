package com.ninjalogan.jarvis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static final String DASHBOARD = "https://ninjaloganf22.github.io/jarvis-dashboard/";
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(2, 6, 13));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(2, 6, 13));
        webView = new WebView(this);
        webView.setBackgroundColor(android.graphics.Color.rgb(2, 6, 13));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setSupportMultipleWindows(false);
        // Always fetch the latest dashboard rather than showing a stale WebView cache.
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearCache(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost() == null ? "" : uri.getHost();
                if (host.equals("ninjaloganf22.github.io") || host.equals("jarvis-ai.logan-forrest2204q.workers.dev")) {
                    return false;
                }
                if (uri.getScheme().equals("https") || uri.getScheme().equals("http")) {
                    try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
                    catch (Exception ignored) {}
                    return true;
                }
                return false;
            }
        });
        setContentView(webView);
        if (savedInstanceState == null) {
            if (!handleSpotifyCallback(getIntent())) webView.loadUrl(DASHBOARD);
        } else {
            webView.restoreState(savedInstanceState);
            handleSpotifyCallback(getIntent());
        }
    }

    private boolean handleSpotifyCallback(Intent intent) {
        if (intent == null || intent.getData() == null) return false;
        Uri data = intent.getData();
        if (!"com.ninjalogan.jarvis".equals(data.getScheme()) ||
                !"callback".equals(data.getHost())) return false;

        String query = data.getEncodedQuery();
        webView.loadUrl(DASHBOARD + (query == null || query.isEmpty() ? "" : "?" + query));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSpotifyCallback(intent);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
