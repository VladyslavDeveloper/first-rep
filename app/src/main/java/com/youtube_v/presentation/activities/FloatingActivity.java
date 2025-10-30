package com.youtube_v.presentation.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.civ3.R;
import com.youtube_v.domain.DownloaderVideo;
import com.youtube_v.domain.SavingManager;
import com.youtube_v.domain.SizeFloatingActivity;
import com.youtube_v.domain.VoiceSearch;
import com.youtube_v.domain.SkipVideoTime;
import com.youtube_v.presentation.vm.FloatingScreenVM;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FloatingActivity extends AppCompatActivity {
    private LinearLayout linearLayout1;
    private WebView webView;
    private Button btnDownload, btnSkipTime, speedBtn, btnMove, btnLoop;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private boolean isLooping = false;
    private Handler handler;


    // Touch handling variables
    private float initialTouchX;
    private float initialTouchY;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatingScreenVM viewModel = new ViewModelProvider(this).get(FloatingScreenVM.class);

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
        btnLoop = view.findViewById(R.id.btnLoop);
        btnSkipTime = view.findViewById(R.id.btnSkipTime);

        viewModel.initializeContent(webView, this);


        // Set button click listeners
        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            windowManager.removeView(view);

            finish();
        });
        view.findViewById(R.id.btnSkipTime).setOnClickListener(v -> viewModel.skipVideoTime(webView));
        // view.findViewById(R.id.btnSpeed).setOnClickListener(v -> SpeedPlayback.cyclePlaybackSpeed(speedBtn, webView, this));
        view.findViewById(R.id.btnLoop).setOnClickListener(v -> {
            isLooping = !isLooping;
            webView.evaluateJavascript("document.querySelector('video').loop = " + isLooping + ";", null);
            btnLoop.setText(isLooping ? "on" : "off");
        });

        //view.findViewById(R.id.btnVoiceSearch1).setOnClickListener(v -> VoiceSearch.startVoiceSearch(this));
        view.findViewById(R.id.btnDownload).setOnClickListener(v -> DownloaderVideo.downloadCurrentVideo(this, webView));

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
        SizeFloatingActivity.setupSizeControl(this, params, windowManager, linearLayout1);
        // Start duration check
        //  TimerExecution.startDurationCheck(webView,this);
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
    }
}
