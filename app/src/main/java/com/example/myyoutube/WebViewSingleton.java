package com.example.myyoutube;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewSingleton {
    private static WebView webView;

    private WebViewSingleton() {}

    public static WebView getWebView(Context context) {
        if (webView == null) {
            webView = new WebView(context);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
        }
        return webView;
    }
}
