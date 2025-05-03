package com.example.myyoutube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnVoiceSearch;

    public static SpeedPlayback speedPlayback;


    private LinearLayout controlsLayout;
    private WebView webView;
    private Button btnSpeed, btnSkip4sec, btnLoop, btnTimer, btnRotate;
    private Button btnRecentVideos;



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
        btnVoiceSearch = findViewById(R.id.btnVoiceSearch);
        speedPlayback = new SpeedPlayback();



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
            PanelVisible.toggleControlsVisibility(controlsLayout, MainActivity.this);
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
        Buttons.makeButtons(this, controlsLayout, MainActivity.this, webView, btnSpeed, btnSkip4sec, btnLoop, btnTimer, btnRotate,btnVoiceSearch,btnRecentVideos);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VoiceSearch.handleResult(requestCode, resultCode, data, this, webView);
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
        ControlOrientationHorizontal.changeOrientation(controlsLayout, webView, MainActivity.this, newConfig);
    }


}