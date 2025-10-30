package com.youtube_v.domain.myyoutube;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.Button;

public class SpeedPlayback {

    static void savePlaybackSpeed(float speed, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SaveAndLoadLastVideo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(SaveAndLoadLastVideo.PREF_SPEED, speed);
        editor.apply();
    }

    public static void applyPlaybackSpeed(float speed, WebView webView) {
        webView.evaluateJavascript("document.querySelector('video').playbackRate = " + speed + ";", null);
    }
}
