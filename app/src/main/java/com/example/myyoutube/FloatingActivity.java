package com.example.myyoutube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.widget.Button;
import android.widget.LinearLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class FloatingActivity extends AppCompatActivity {

    private LinearLayout linearLayout1;
    private WebView webView;
    private Button btnDownload, btnSkipTime, speedBtn, btnMove, btnLoop;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private boolean isLooping = false;
    private float initialTouchX, initialTouchY;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize WindowManager and LayoutParams
        initWindowManager();

        // Inflate the floating layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_floating, null);
        windowManager.addView(view, params);

        // Initialize UI elements
        initUI(view);

        // Setup WebView
        SaveAndLoadLastVideo.initializeWebView(webView, this);

        // Set button click listeners
        setButtonListeners(view);

        // Setup move button touch listener
        setupMoveButton(view);

        // Setup size control
        SizeFloatingActivity.setupSizeControl(this, params, windowManager, linearLayout1);

        // Start duration check
        TimerExecution.startDurationCheck(webView, this);
    }

    // Initialize the WindowManager and LayoutParams
    private void initWindowManager() {
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
    }

    // Initialize UI elements
    private void initUI(View view) {
        linearLayout1 = view.findViewById(R.id.linearLayout1);
        webView = view.findViewById(R.id.webView);
        btnDownload = view.findViewById(R.id.btnDownload);
        btnMove = view.findViewById(R.id.btnMove);
        speedBtn = view.findViewById(R.id.btnSpeed);
        btnLoop = view.findViewById(R.id.btnLoop);
        btnSkipTime = view.findViewById(R.id.btnSkipTime);
    }

    // Set button click listeners
    private void setButtonListeners(View view) {
        // Close button
        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            windowManager.removeView(view);
            finish();
        });

        // Skip Time button
        view.findViewById(R.id.btnSkipTime).setOnClickListener(v -> ShowSkipDialog.skipThreeMinutes(webView));

        // Speed button
        view.findViewById(R.id.btnSpeed).setOnClickListener(v -> SpeedPlayback.cyclePlaybackSpeed(speedBtn, webView, this));

        // Loop button
        view.findViewById(R.id.btnLoop).setOnClickListener(v -> {
            isLooping = !isLooping;
            webView.evaluateJavascript("document.querySelector('video').loop = " + isLooping + ";", null);
            btnLoop.setText(isLooping ? "on" : "off");
        });

        // Voice Search button
        view.findViewById(R.id.btnVoiceSearch1).setOnClickListener(v -> VoiceSearch.startVoiceSearch(this));

        // Download button
        view.findViewById(R.id.btnDownload).setOnClickListener(v -> DownloadVideo.downloadCurrentVideo(this, webView));
    }

    // Setup move button touch listener
    private void setupMoveButton(View view) {
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Reload video URL from new intent
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VoiceSearch.handleResult(requestCode, resultCode, data, this, webView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windowManager != null && linearLayout1 != null) {
            windowManager.removeView(linearLayout1);
        }
    }
}
