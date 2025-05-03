package com.example.myyoutube;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

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

        Button btnToggleControls = activity.findViewById(R.id.btnToggleControls);

        // Initialize a flag to distinguish between move and click
        final long[] downTime = new long[1];

        btnToggleControls.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Record the initial touch position relative to the button's position
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        // Save the time when the touch event starts
                        downTime[0] = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Move the button based on the current touch position
                        v.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        // If the button was pressed for too short a time (like a tap), trigger a click function
                        if (System.currentTimeMillis() - downTime[0] < 200) { // 200ms threshold for tap
                            PanelVisible.toggleControlsVisibility(controlsLayout,activity);  // Call your function
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });

        btnToggleControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click action here (if not already handled by touch listener)
                PanelVisible.toggleControlsVisibility(controlsLayout, activity);
            }
        });

        btnSkip4sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkipDialog.skipThreeMinutes();
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
                isLooping = !isLooping;
                webView.evaluateJavascript("document.querySelector('video').loop = " + isLooping + ";", null);
                btnLoop.setText(isLooping ? "on" : "off");
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
            @Override
            public void onClick(View v) {
                ControlOrientationHorizontal.toggleOrientation(activity);
            }
        });
    }
}
