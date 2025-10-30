package com.youtube_v.domain.myyoutube;

import android.webkit.WebView;

public class SpeedPlayback {

    public static void applyPlaybackSpeed(float speed, WebView webView) {
        webView.evaluateJavascript("document.querySelector('video').playbackRate = " + speed + ";", null);
    }
}
