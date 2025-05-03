package com.example.myyoutube;
import android.os.PowerManager;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.DownloadManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;

import java.util.ArrayList;

public class FloatingActivity extends AppCompatActivity {
    private LinearLayout linearLayout1;
    private WebView webView;
   private Button btnDownload, speedBtn;
    private Button btnMove;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private float playbackSpeed = 1.0f; // Default playback speed

     private int skipTime = 180; // Default skip time (3 minutes)
    private boolean isLooping = false;
    private Handler handler;

    private static final int VOICE_SEARCH_REQUEST_CODE = 100;


    private static final String PREFS_NAME = "MyYouTubePrefs";
    private static final String PREF_URL = "LastVideoUrl";
    private static final String TAG = "FloatingActivity";

    // Touch handling variables
    private float initialTouchX;
    private float initialTouchY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize WindowManager and LayoutParams
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        params = new WindowManager.LayoutParams(
                (int) (SizeFloatingActivity.STATIC_WIDTH * getResources().getDisplayMetrics().density),
                (int) (screenHeight * SizeFloatingActivity.DEFAULT_HEIGHT_PERCENT),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        // Inflate the floating layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_floating, null);
        windowManager.addView(view, params);


        // Initialize UI elements
        linearLayout1 = view.findViewById(R.id.linearLayout1);
        webView = view.findViewById(R.id.webView);
        btnDownload = view.findViewById(R.id.btnDownload);
        btnMove = view.findViewById(R.id.btnMove);
        speedBtn = view.findViewById(R.id.btnSpeed);

        // Setup WebView
        SaveAndLoadLastVideo.initializeWebView(webView,this);



        // Set button click listeners
        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            windowManager.removeView(view);

            finish();
        });
        view.findViewById(R.id.btnAction2).setOnClickListener(v -> skipThreeMinutes());
        view.findViewById(R.id.btnSpeed).setOnClickListener(v -> SpeedPlayback.cyclePlaybackSpeed(speedBtn,webView,this));
        view.findViewById(R.id.btnLoop).setOnClickListener(v -> toggleLooping());
        view.findViewById(R.id.btnVoiceSearch1).setOnClickListener(v -> VoiceSearch.startVoiceSearch(this));
        view.findViewById(R.id.btnDownload).setOnClickListener(v -> DownloadVideo.downloadCurrentVideo(this,webView));

        // Setup move button touch listener
        btnMove.setVisibility(View.VISIBLE); // Make move button visible

        btnMove.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        btnMove.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - initialTouchX;
                        float deltaY = event.getRawY() - initialTouchY;

                        params.x += deltaX;
                        params.y += deltaY;

                        windowManager.updateViewLayout(linearLayout1, params);

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        btnMove.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5F6060")));
                        return true;
                }
                return false;
            }
        });

        // Setup size control
        SizeFloatingActivity.setupSizeControl(this,params,windowManager,linearLayout1);
        // Start duration check
        startDurationCheck();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Reload video URL from new intent

    }





    private void skipThreeMinutes() {
        webView.evaluateJavascript("document.querySelector('video').currentTime += " + skipTime + ";", null);
    }

    private void startDurationCheck() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SkipaAdd.checkAndSkipAdsF(webView);
                SkipaAdd.skipVideoF(webView);
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }




    private void toggleLooping() {
        // Toggle the looping state
        isLooping = !isLooping;

        // Execute JavaScript to toggle the loop property of the video element
        webView.evaluateJavascript("document.querySelector('video').loop = " + isLooping + ";", null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VoiceSearch.handleResult(requestCode, resultCode, data, this, webView);
    }











}
