package com.example.myyoutube;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Button;

public class SpeedPlayback {
    private final int speedUpdateInterval = 2000;
    private Handler handler;
    public static boolean isTimerRunning = true;


    public static void cyclePlaybackSpeed(Button btnSpeed, MainActivity mainActivity,Context context) {
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
        btnSpeed.setText(SaveAndLoadLastVideo.playbackSpeed + "x Speed");
        savePlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed, context);
        mainActivity.applyPlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed);
    }

    private static void savePlaybackSpeed(float speed, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SaveAndLoadLastVideo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(SaveAndLoadLastVideo.PREF_SPEED, speed);
        editor.apply();
    }
    public void startSpeedUpdateTimer(MainActivity mainActivity) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTimerRunning) {
                    mainActivity.applyPlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed);
                    handler.postDelayed(this, speedUpdateInterval);
                }
            }
        }, speedUpdateInterval);
    }
}
