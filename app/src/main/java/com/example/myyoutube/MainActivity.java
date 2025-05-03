package com.example.myyoutube;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 101;
    final float MAX_PLAYBACK_RATE = 3.0f;
    private Button btnVoiceSearch;
    private SkipaAdd skipaAdd;
    private ShowSkipDialog showSkipDialog;
    private static final int ONE_MINUTE = 60;
    private static final int TWO_MINUTES = 300;
    private static final int THREE_MINUTES = 600;
    private static final int FIVE_MINUTES = 900;
    private boolean isControlVisible = true;  // Инициализация переменной
    private LinearLayout controlsLayout;
    private WebView webView;
    private VoiceSearch voiceSearch;
    private Button btnSpeed, btnSkip4sec, btnLoop, btnTimer, btnRotate;
    private Button btnRecentVideos;
    private Handler handler;
    private boolean isLooping = false;
    private boolean isTimerRunning = true;
    private int skipTime = THREE_MINUTES; // Set skip time to 3 minutes in seconds
    private final int speedUpdateInterval = 2000;
    private boolean isLandscape = false;

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

        showSkipDialog = new ShowSkipDialog(this, webView);
        skipaAdd = new SkipaAdd(this, webView);
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
        SaveAndLoadLastVideo.initializeWebView(webView, this, this);

        setupButtonListeners();
        startSpeedUpdateTimer();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            // Восстанавливаем данные
            isControlVisible = savedInstanceState.getBoolean("isControlVisible");

            // Применяем состояние (например, показываем или скрываем элементы управления)
            toggleControlsVisibility();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Сохраняем необходимые данные, например:
        outState.putBoolean("isControlVisible", isControlVisible);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setupButtonListeners() {
        btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cyclePlaybackSpeed();
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
                            toggleControlsVisibility();  // Call your function
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
                toggleControlsVisibility();
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
                checkOverlayPermission();
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
                isTimerRunning = !isTimerRunning;
                if (isTimerRunning) {
                    startSpeedUpdateTimer();
                    btnTimer.setText("on");
                } else {
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    }
                    btnTimer.setText("off");
                }
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOrientation();
            }
        });
    }

    private void cyclePlaybackSpeed() {
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
        savePlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed);
        applyPlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        voiceSearch.handleActivityResult(requestCode, resultCode, data);
    }

    private void savePlaybackSpeed(float speed) {
        SharedPreferences preferences = getSharedPreferences(SaveAndLoadLastVideo.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(SaveAndLoadLastVideo.PREF_SPEED, speed);
        editor.apply();
    }

    public void applyPlaybackSpeed(float speed) {
        webView.evaluateJavascript("document.querySelector('video').playbackRate = " + speed + ";", null);
    }

    private void startSpeedUpdateTimer() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isTimerRunning) {
                    applyPlaybackSpeed(SaveAndLoadLastVideo.playbackSpeed);
                    handler.postDelayed(this, speedUpdateInterval);
                }
            }
        }, speedUpdateInterval);
    }

    public void startDurationCheck() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SaveAndLoadLastVideo.shouldCheckDuration) {
                    skipaAdd.skipVideo();
                    skipaAdd.checkVideoDuration();
                    SaveAndLoadLastVideo.saveLastVideoUrl(webView.getUrl(), MainActivity.this);
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void visibleOf() {
        controlsLayout.setVisibility(View.GONE);
        Joystick.joystickView.setVisibility(View.GONE);
        // Set video container height to 0dp
        FrameLayout videoContainer = findViewById(R.id.controls_scroll);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
        params.height = 0; // Set height to 0dp
        videoContainer.setLayoutParams(params);
    }

    private void toggleControlsVisibility() {
        if (controlsLayout.getVisibility() == View.VISIBLE) {
            visibleOf();
        } else {
            controlsLayout.setVisibility(View.VISIBLE);
            Joystick.joystickView.setVisibility(View.VISIBLE);
            // Optionally set video container height back to wrap_content or desired height
            FrameLayout videoContainer = findViewById(R.id.controls_scroll);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
            params.height = 234;
            videoContainer.setLayoutParams(params);
        }
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                startFloatingActivity();
            }
        } else {
            startFloatingActivity();
        }
    }

    private void startFloatingActivity() {
        String currentUrl = webView.getUrl(); // Get current URL from WebView
        Intent intent = new Intent(this, FloatingActivity.class);
        intent.putExtra("video_url", currentUrl); // Pass URL in Intent
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Close MainActivity
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

    private void toggleOrientation() {
        isLandscape = !isLandscape;
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);


        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            JavaScript.videoFullScreen(webView);

            visibleOf();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            JavaScript.videoFullScreenCancel(webView);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            toggleControlsVisibility();
        }
    }


}