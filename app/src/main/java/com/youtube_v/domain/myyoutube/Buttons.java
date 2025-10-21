package com.youtube_v.domain.myyoutube;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.civ3.R;
import com.youtube_v.domain.use_cases.OpenFloatingActivity;
import com.youtube_v.domain.use_cases.ShowSkipDialog;
import com.youtube_v.presentation.activities.MainActivity;


public class Buttons {
    private static ShowSkipDialog showSkipDialog;
    private static boolean isLooping = false;
    public  static  void  makeButtons(Context context,View controlsLayout, Activity activity, WebView webView, Button btnSpeed, Button btnSkip4sec, Button btnLoop, Button btnTimer, Button btnRotate, Button btnVoiceSearch, Button btnRecentVideos){

        showSkipDialog = new ShowSkipDialog(activity, webView);
        btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeedPlayback.cyclePlaybackSpeed(btnSpeed,webView, activity);
            }
        });

        btnSkip4sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkipDialog.skipThreeMinutes(webView);
            }
        });

        Button btnOpenFloating = activity.findViewById(R.id.btnOpenFloating);
        btnOpenFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFloatingActivity.checkOverlayPermission(context,activity,webView);
            }
        });
        btnRecentVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LookLastVideo.showSearchChannel(activity, webView);
            }
        });
        btnSkip4sec.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSkipDialog.showSkipTimeDialog();
                return true;
            }
        });

        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeedPlayback.isTimerRunning = !SpeedPlayback.isTimerRunning;
                if (SpeedPlayback.isTimerRunning) {
                    MainActivity.speedPlayback.startSpeedUpdateTimer(webView);
                    btnTimer.setText("on");
                } else {
                    if (TimerExecution.handler != null) {
                        TimerExecution.handler.removeCallbacksAndMessages(null);
                    }
                    btnTimer.setText("off");
                }
            }
        });




        btnVoiceSearch.setOnClickListener(v -> VoiceSearch.startVoiceSearch(activity));
        btnRotate.setOnClickListener(new View.OnClickListener() {
            boolean isHorizontal = true; // по умолчанию включена

            @Override
            public void onClick(View v) {
                isHorizontal = !isHorizontal;

                if (isHorizontal) {
                    btnRotate.setText("\uD83D\uDD04❌"); // включено
                } else {
                    btnRotate.setText("\uD83D\uDD04✅"); // выключено
                }
            }
        });

    }
}
