package com.youtube_v.domain.myyoutube;

import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;

public class TimerExecution {
    public static Handler handler;
    private static SkipAdd skipAdd;

    public static void startDurationCheck(WebView webView, Context context) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                skipAdd = new SkipAdd(webView);
                skipAdd.skipVideo();
                skipAdd.checkVideoDuration();
                SavingManager.saveLastVideoUrl(webView.getUrl(), context);
                SavingManager.loadPlayBackSpeed(context, webView);
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

}
