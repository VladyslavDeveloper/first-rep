package com.example.myyoutube;

import android.annotation.SuppressLint;
import android.app.PendingIntent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 101;
    private static final int VOICE_SEARCH_REQUEST_CODE = 1000;
    private Button btnVoiceSearch;
    static final String PREFS_NAME = "WebViewPrefs";
    static final String PREF_URL = "url";
    // In both MainActivity and FloatingActivity
    public static final String PREF_SPEED = "playback_speed";
    private SkipaAdd skipaAdd;
    private ShowSkipDialog showSkipDialog ;
    private static final int ONE_MINUTE = 60;
    private static final int TWO_MINUTES = 300;
    private static final int THREE_MINUTES = 600;
    private static final int FIVE_MINUTES = 900;
    private boolean isControlVisible = true;  // Инициализация переменной
    private LinearLayout controlsLayout;

    private WebView webView;
    private Button btnSpeed, btnSkip4sec, btnLoop, btnTimer;
    private Handler handler;
    private boolean shouldCheckDuration = false;
    private boolean isLooping = false;

    private boolean isTimerRunning = true;
    private int skipTime = THREE_MINUTES; // Set skip time to 3 minutes in seconds
    private float playbackSpeed = 1.0f;
    private final int speedUpdateInterval = 2000;

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controlsLayout = findViewById(R.id.controls);


        webView = findViewById(R.id.webview);
        btnSpeed = findViewById(R.id.btnSpeed);
        btnSkip4sec = findViewById(R.id.btnSkip3min); // Renamed to reflect 3-minute skip
        btnLoop = findViewById(R.id.btnLoop);
        btnTimer = findViewById(R.id.btnTimer);

        showSkipDialog = new ShowSkipDialog(this,webView);
        skipaAdd = new SkipaAdd(this, webView);


        btnVoiceSearch = findViewById(R.id.btnVoiceSearch);

        btnVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceSearch();
            }
        });



        // Initialize the WebView and load last saved URL
        initializeWebView();

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


    public void initializeWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Enable cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                shouldCheckDuration = true;
                startDurationCheck();
                saveLastVideoUrl(url);
                applyPlaybackSpeed(playbackSpeed);
            }
        });





        // Load the last saved URL and playback speed
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastVideoUrl = preferences.getString(PREF_URL, "http://www.youtube.com");
        playbackSpeed = preferences.getFloat(PREF_SPEED, 1.0f);
        webView.loadUrl(lastVideoUrl);
    }
    private void startVoiceSearch() {
        // Create an Intent for voice search
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to search on YouTube");

        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech recognition is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLastVideoUrl(String url) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_URL, url);
        editor.apply();
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
                skipThreeMinutes();
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
    }

    private void cyclePlaybackSpeed() {
        switch ((int) playbackSpeed) {
            case 1:
                playbackSpeed = 2.0f;
                break;
            case 2:
                playbackSpeed = 3.0f;
                break;
            case 3:
                playbackSpeed = 1.0f;
                break;
        }
        btnSpeed.setText(playbackSpeed + "x Speed");
        savePlaybackSpeed(playbackSpeed);
        applyPlaybackSpeed(playbackSpeed);
    }







    private void skipThreeMinutes() {
        webView.evaluateJavascript("document.querySelector('video').currentTime += 180;", null);
    }

    private void savePlaybackSpeed(float speed) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(PREF_SPEED, speed);
        editor.apply();
    }

    private void applyPlaybackSpeed(float speed) {
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
                    applyPlaybackSpeed(playbackSpeed);
                    handler.postDelayed(this, speedUpdateInterval);
                }
            }
        }, speedUpdateInterval);
    }

    private void startDurationCheck() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (shouldCheckDuration) {
                    skipaAdd.checkVideoDuration();
                    skipaAdd.skipVideo();
                    saveLastVideoUrl(webView.getUrl());
                    handler.postDelayed(this, 1000); // Check every 3 seconds
                }
            }
        }, 1000); // Initial delay of 3 seconds before first check
    }





    private void toggleControlsVisibility() {
        if (controlsLayout.getVisibility() == View.VISIBLE) {
            controlsLayout.setVisibility(View.GONE);
        } else {
            controlsLayout.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from the voice search request
        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the voice search results from the intent
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                // Get the first result from the voice search
                String query = results.get(0);

                // Load the YouTube search URL in the WebView
                WebView webView = findViewById(R.id.webview); // Reference to the WebView
                String searchUrl = "https://www.youtube.com/results?search_query=" + query;

                // Load the search URL in the WebView
                webView.loadUrl(searchUrl);
            } else {
                Toast.makeText(this, "No voice input detected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startFloatingActivity() {
        String currentUrl = webView.getUrl(); // Получаем текущий URL из WebView
        Intent intent = new Intent(this, FloatingActivity.class);
        intent.putExtra("video_url", currentUrl); // Передаем URL в Intent
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Go back to the previous page in WebView history
        } else {
            super.onBackPressed(); // Exit the activity if there's no history
        }
    }
}