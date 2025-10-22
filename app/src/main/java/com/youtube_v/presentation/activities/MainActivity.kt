package com.youtube_v.presentation.activities;

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

import com.civ3.R;
import com.youtube_v.domain.myyoutube.Buttons;
import com.youtube_v.domain.myyoutube.SaveAndLoadLastVideo;
import com.youtube_v.domain.myyoutube.SpeedPlayback;
import com.youtube_v.domain.myyoutube.VoiceSearch;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private Button btnSpeed, btnLoop, btnTimer, btnSecActivity;
    private Button btnRecentVideos;


    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        webView = findViewById(R.id.webview);
        btnSpeed = findViewById(R.id.btnSpeed);
         btnLoop = findViewById(R.id.btnLoop);
        btnTimer = findViewById(R.id.btnTimer);
         btnRecentVideos = findViewById(R.id.btnRecentVideos);

        btnSecActivity = findViewById(R.id.btnOpenSecondActivity);



        setupButtonListeners();

        btnSecActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setupButtonListeners() {
        Buttons.makeButtons(MainActivity.this, webView, btnSpeed, btnLoop, btnTimer, btnRecentVideos);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}