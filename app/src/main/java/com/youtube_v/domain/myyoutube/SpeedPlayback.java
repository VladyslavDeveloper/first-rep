package com.youtube_v.domain.myyoutube;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.webkit.WebView;
import android.widget.Button;

public class SpeedPlayback {
    private final int speedUpdateInterval = 2000;
    private Handler handler;
    public static boolean isTimerRunning = true;


    public static void cyclePlaybackSpeed(Button btnSpeed, WebView webView,Context context) {
        switch ((int) SaveAndLoadLastVideo.playbackSpeed) {
            case 1:
                SaveAndLoadLastVideo.playbackSpeed = 2.0f;
                break;
            case 2:
                SaveAndLoadLastVideo.playbackSpeed = 3.0f;
                break;
            case 3:
                SaveAndLoadLastVideo.playbackSpeed = 1.0f;
                break;
        }
        btnSpeed.setText(SaveAndLoadLastVideo.playbackSpeed + "");
        savePlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed, context);
        applyPlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed, webView);
    }

    private static void savePlaybackSpeed(float speed, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SaveAndLoadLastVideo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(SaveAndLoadLastVideo.PREF_SPEED, speed);
        editor.apply();
    }

    public static void applyPlaybackSpeed(float speed, WebView webView) {
        webView.evaluateJavascript("document.querySelector('video').playbackRate = " + speed + ";", null);
    }
}
