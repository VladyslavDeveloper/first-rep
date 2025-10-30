package com.youtube_v.domain;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.youtube_v.domain.core.AppConstants;

public class SavingManager {

    public static void initializeWebView(WebView webView,Context context) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Enable cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                TimerExecution.startDurationCheck(webView,context);
                JavaScriptExecutor.applyPlaybackSpeed(1f, webView);
                JavaScriptExecutor.makeSubtitleOf(webView);


            }
        });
       loadSavedURL(context, webView);
    }

    public static void saveLastVideoUrl(String url, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstants.PREF_URL, url);
        editor.apply();
    }
    private static void loadSavedURL(Context context, WebView webView){
        // Load the last saved URL
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFS_NAME, MODE_PRIVATE);
        String lastVideoUrl = preferences.getString(AppConstants.PREF_URL, AppConstants.BASE_URL);
        webView.loadUrl(lastVideoUrl);
    }

    public static void savePlayBackSpeed(SharedPreferences prefs, float speed){
        prefs.edit()
                .putFloat(AppConstants.PREF_SPEED, speed)
                .apply();

    }
    public static void loadPlayBackSpeed(Context context, WebView webView){
        // Load the last playback speed
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREFS_NAME, MODE_PRIVATE);
        float playbackSpeed = preferences.getFloat(AppConstants.PREF_SPEED, 1.0f);
        JavaScriptExecutor.applyPlaybackSpeed(playbackSpeed, webView);
    }
}
