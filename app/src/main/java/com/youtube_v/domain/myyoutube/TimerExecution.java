package com.youtube_v.domain.myyoutube;

import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;

public class TimerExecution {
    public static Handler handler;
    private static SkipaAdd skipaAdd;
    public static void startDurationCheck(WebView webView, Context context) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SaveAndLoadLastVideo.shouldCheckDuration) {
                    skipaAdd = new SkipaAdd(context, webView);
                    skipaAdd.skipVideo();
                    skipaAdd.checkVideoDuration();
                    SaveAndLoadLastVideo.saveLastVideoUrl(webView.getUrl(), context);
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

}
