package com.example.myyoutube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    final float MAX_PLAYBACK_RATE = 3.0f;
    private Button btnVoiceSearch;

    SpeedPlayback speedPlayback;
    private ShowSkipDialog showSkipDialog;
    private static final int ONE_MINUTE = 60;
    private static final int TWO_MINUTES = 300;
    private static final int THREE_MINUTES = 600;
    private static final int FIVE_MINUTES = 900;
     private LinearLayout controlsLayout;
    private WebView webView;
    private VoiceSearch voiceSearch;
    private Button btnSpeed, btnSkip4sec, btnLoop, btnTimer, btnRotate;
    private Button btnRecentVideos;

    private boolean isLooping = false;

    private int skipTime = THREE_MINUTES; // Set skip time to 3 minutes in seconds

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controlsLayout = findViewById(R.id.controls);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        webView = findViewById(R.id.webview);
        btnSpeed = findViewById(R.id.btnSpeed);
        btnSkip4sec = findViewById(R.id.btnSkip3min);
        btnLoop = findViewById(R.id.btnLoop);
        btnTimer = findViewById(R.id.btnTimer);
        btnRotate = findViewById(R.id.btnRotate);
        btnRecentVideos = findViewById(R.id.btnRecentVideos);
        speedPlayback = new SpeedPlayback();

        showSkipDialog = new ShowSkipDialog(this, webView);
        voiceSearch = new VoiceSearch(this, webView);

        btnVoiceSearch = findViewById(R.id.btnVoiceSearch);

        btnVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceSearch.startVoiceSearch();
            }
        });

        // Set click listener for recent videos button
        btnRecentVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LookLastVideo.showSearchChannel(MainActivity.this, webView);
            }
        });

        Joystick.joystickView = findViewById(R.id.joystickView);
        Joystick.setupJoystickControl(webView);

        // Initialize the WebView and load last saved URL
        SaveAndLoadLastVideo.initializeWebView(webView, this);

        setupButtonListeners();
        speedPlayback.startSpeedUpdateTimer(webView);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            // Восстанавливаем данные
            PanelVisible.isControlVisible = savedInstanceState.getBoolean("isControlVisible");

            // Применяем состояние (например, показываем или скрываем элементы управления)
            PanelVisible.toggleControlsVisibility(controlsLayout,MainActivity.this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Сохраняем необходимые данные, например:
        outState.putBoolean("isControlVisible", PanelVisible.isControlVisible);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setupButtonListeners() {
        btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeedPlayback.cyclePlaybackSpeed(btnSpeed,webView, MainActivity.this);
            }
        });

        Button btnToggleControls = findViewById(R.id.btnToggleControls);

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
                            PanelVisible.toggleControlsVisibility(controlsLayout,MainActivity.this);  // Call your function
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
                PanelVisible.toggleControlsVisibility(controlsLayout, MainActivity.this);
            }
        });

        btnSkip4sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSkipDialog.skipThreeMinutes();
            }
        });

        Button btnOpenFloating = findViewById(R.id.btnOpenFloating);
        btnOpenFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFloatingActivity.checkOverlayPermission(MainActivity.this,MainActivity.this,webView);
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
                    speedPlayback.startSpeedUpdateTimer(webView);
                    btnTimer.setText("on");
                } else {
                    if (TimerExecution.handler != null) {
                        TimerExecution.handler.removeCallbacksAndMessages(null);
                    }
                    btnTimer.setText("off");
                }
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControlOrientationHorizontal.toggleOrientation(MainActivity.this);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        voiceSearch.handleActivityResult(requestCode, resultCode, data);
    }








    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle any new intents here if needed
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Go back to the previous page in WebView history
        } else {
            super.onBackPressed(); // Exit the activity if there's no history
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Joystick.joystickHandler != null) {
            Joystick.joystickHandler.removeCallbacks(Joystick.joystickRunnable);
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ControlOrientationHorizontal.changeOrientation(controlsLayout,webView,MainActivity.this,newConfig);
    }


}