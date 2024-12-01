package com.example.myyoutube;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FloatingActivity extends AppCompatActivity {
    private LinearLayout linearLayout1;
    private WebView webView;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private float playbackSpeed = 1.0f; // Default playback speed

    private boolean isFullScreen = false; // Track layout state
    private int skipTime = 180; // Default skip time (3 minutes)
    private boolean isLooping = false;
    private Handler handler;

    private static final int VOICE_SEARCH_REQUEST_CODE = 100;


    private static final String PREFS_NAME = "MyYouTubePrefs";
    private static final String PREF_URL = "LastVideoUrl";
    private static final String TAG = "FloatingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize WindowManager and LayoutParams
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
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

        // Setup WebView
        initializeWebView();
        loadVideoUrlFromIntent();

        // Set touch listener for moving the window
        view.setOnTouchListener(new View.OnTouchListener() {
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Calculate the distance moved
                        float deltaX = event.getRawX() - initialTouchX;
                        float deltaY = event.getRawY() - initialTouchY;

                        // Update the window position
                        params.x += deltaX;
                        params.y += deltaY;

                        // Update the view layout with the new position
                        windowManager.updateViewLayout(view, params);

                        // Update the initial touch point for the next move
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        return true;
                }
                return false;
            }
        });


        // Set button click listeners
        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            windowManager.removeView(view);
            finish();
        });
        view.findViewById(R.id.btnAction2).setOnClickListener(v -> skipThreeMinutes());
         view.findViewById(R.id.btnSpeed).setOnClickListener(v -> showSpeedDialog());
        view.findViewById(R.id.btnLoop).setOnClickListener(v -> toggleLooping());
        view.findViewById(R.id.btnResize).setOnClickListener(v -> toggleSize());
        view.findViewById(R.id.btnVoiceSearch1).setOnClickListener(v -> startVoiceSearch());

        // Start duration check
        startDurationCheck();
    }
    private void skipVideo() {
        webView.evaluateJavascript(
                "(function() {" +
                        "var video = document.querySelector('video');" +
                        "if (video) {" +
                        "return video.duration;" +
                        "} else {" +
                        "return 0;" +
                        "}" +
                        "})();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        try {
                            double duration = Double.parseDouble(value);
                            if (duration < 25) { // If video is shorter than 4 seconds
                                // Skip forward 3 minutes (180 seconds)
                                webView.evaluateJavascript(
                                        "var video = document.querySelector('video');" +
                                                "if (video) {" +
                                                "var newTime = video.currentTime + 180;" + // Skip forward 3 minutes
                                                "if (newTime < video.duration) {" +
                                                "video.currentTime = newTime;" +
                                                "} else {" +
                                                "video.currentTime = video.duration;" +
                                                "}" +
                                                "}", null);
                            } else {

                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing video duration: " + e.getMessage());
                        }
                    }
                });
    }

    private void initializeWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });
    }

    private void loadVideoUrlFromIntent() {
        // Get the video URL passed from MainActivity
        String videoUrl = getIntent().getStringExtra("video_url");
        if (videoUrl != null && !videoUrl.isEmpty()) {
            webView.loadUrl(videoUrl); // Load the video URL in WebView
        } else {
            webView.loadUrl("https://www.youtube.com"); // Default to YouTube if no URL is passed
        }
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
                checkAndSkipAds();
                skipVideo();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void checkAndSkipAds() {
        // Check and skip ads (if applicable)
        webView.evaluateJavascript("javascript:(function() {" +
                "const skipButton = document.querySelector('button[class*=\"ytp-ad-skip-button\"]');" +
                "if (skipButton) {" +
                "skipButton.click();" + // Clicking the skip ad button
                "const video = document.querySelector('video');" +
                "if (video) {" +
                "video.currentTime += 1900; " + // Skip forward by 40 seconds
                "return 'Реклама пропущена, видео перемотано на 1900 секунд';" +
                "} else {" +
                "return 'Видео не найдено, но реклама пропущена';" +
                "}" +
                "} else {" +
                "return 'Кнопка пропуска рекламы не найдена';" +
                "}" +
                "})();", value -> {
            if (value != null && !value.equals("null")) {

            }
        });

    }


    private void showSpeedDialog() {
        // Cycle through the speeds: 1x -> 2x -> 3x -> 1x
        if (playbackSpeed == 1.0f) {
            playbackSpeed = 2.0f; // Change to 2x
        } else if (playbackSpeed == 2.0f) {
            playbackSpeed = 3.0f; // Change to 3x
        } else {
            playbackSpeed = 1.0f; // Change back to 1x
        }

        applyPlaybackSpeed(playbackSpeed);
          }


    private void applyPlaybackSpeed(float speed) {
        webView.evaluateJavascript("document.querySelector('video').playbackRate = " + speed + ";", null);
    }

    private void toggleLooping() {
        // Toggle the looping state
        isLooping = !isLooping;

        // Execute JavaScript to toggle the loop property of the video element
        webView.evaluateJavascript("document.querySelector('video').loop = " + isLooping + ";", null);


    }


    private void startVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to search on YouTube");

        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech recognition is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                openYouTubeSearch(results.get(0));
            } else {
                Toast.makeText(this, "No voice input detected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openYouTubeSearch(String query) {
        String searchUrl = "https://www.youtube.com/results?search_query=" + query.replace(" ", "+");
        webView.loadUrl(searchUrl);
    }


    private void toggleSize() {
        if (isFullScreen) {
            int newHeight = (int) (100 * getResources().getDisplayMetrics().density);
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = newHeight;
        } else {
            // Set to full screen
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        windowManager.updateViewLayout(linearLayout1, params);
        isFullScreen = !isFullScreen;
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
