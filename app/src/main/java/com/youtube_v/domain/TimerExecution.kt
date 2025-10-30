package com.youtube_v.domain;

import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;

import com.youtube_v.domain.SavingManager;
import com.youtube_v.domain.SkipAd;

public class TimerExecution {
    public static Handler handler;
    private static SkipAd skipAd;

    public static void startDurationCheck(WebView webView, Context context) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                skipAd = new SkipAd(webView);
                skipAd.checkIfVideoExists();
                SavingManager.saveLastVideoUrl(webView.getUrl(), context);
                SavingManager.loadPlayBackSpeed(context, webView);
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
}
