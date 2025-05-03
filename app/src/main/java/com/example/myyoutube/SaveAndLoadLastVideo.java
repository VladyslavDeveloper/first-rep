package com.example.myyoutube;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SaveAndLoadLastVideo {
    public static final String PREF_SPEED = "playback_speed";
    static final String PREFS_NAME = "WebViewPrefs";
    static final String PREF_URL = "url";
    public static boolean shouldCheckDuration = false;
    public static float playbackSpeed = 1.0f;

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
                shouldCheckDuration = true;
                TimerExecution.startDurationCheck(webView,context);
                SaveAndLoadLastVideo.saveLastVideoUrl(url,context);
                SpeedPlayback.applyPlaybackSpeed(playbackSpeed, webView);

                JavaScript.makeSubtitleOf(webView);

            }
        });

        // Load the last saved URL and playback speed
        SharedPreferences preferences = context.getSharedPreferences(SaveAndLoadLastVideo.PREFS_NAME, MODE_PRIVATE);
        String lastVideoUrl = preferences.getString(SaveAndLoadLastVideo.PREF_URL, "http://www.youtube.com");
        playbackSpeed = preferences.getFloat(PREF_SPEED, 1.0f);
        webView.loadUrl(lastVideoUrl);
    }

    public static void saveLastVideoUrl(String url, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_URL, url);
        editor.apply();
    }
}
