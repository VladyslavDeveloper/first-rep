package com.youtube_v.presentation.activities;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.os.Bundle;
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
import com.youtube_v.presentation.vm.FloatingScreenVM;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FloatingActivity extends AppCompatActivity {
    private LinearLayout linearLayout_main;
    private WebView webView;
    private Button btnMove;
    private float dx, dy;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatingScreenVM viewModel = new ViewModelProvider(this).get(FloatingScreenVM.class);

        // Initialize WindowManager and LayoutParams
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                777,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // для поверх других приложений
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // Inflate the floating layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_floating, null);
        windowManager.addView(view, params);


        // Initialize UI elements
        linearLayout_main = view.findViewById(R.id.linearLayout_main);
        webView = view.findViewById(R.id.webView);
        btnMove = view.findViewById(R.id.btnMove);

        viewModel.initializeContent(webView, this);


        // Set button click listeners
        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            windowManager.removeView(view);
            finish();
        });
        view.findViewById(R.id.btnDownload).setOnClickListener(v -> DownloaderVideo.downloadCurrentVideo(this, webView));

        btnMove.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dx = e.getRawX() - params.x;
                    dy = e.getRawY() - params.y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    params.x = (int) (e.getRawX() - dx);
                    params.y = (int) (e.getRawY() - dy);
                    windowManager.updateViewLayout(view, params);
                    return true;
            }
            return false;
        });
    }
}
