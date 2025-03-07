package com.example.myyoutube;
import android.os.PowerManager;

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

    private int currentSizeState = 0; // 0: small, 1: medium, 2: large, 3: full screen
    private boolean isFullScreen = false; // Track layout state
    private int skipTime = 180; // Default skip time (3 minutes)
    private boolean isLooping = false;
    private Handler handler;

    private static final int VOICE_SEARCH_REQUEST_CODE = 100;


    private static final String PREFS_NAME = "MyYouTubePrefs";
    private static final String PREF_URL = "LastVideoUrl";
    private static final String TAG = "FloatingActivity";
    private static final int RESIZE_HANDLE_SIZE = 48; // dp
    private boolean isResizing = false;
    private float initialTouchX, initialTouchY;
    private int initialWidth, initialHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();


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

        // Set touch listener for moving and resizing the window
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        initialWidth = params.width;
                        initialHeight = params.height;
                        
                        // Check if touch is in resize area (bottom-right corner)
                        isResizing = isTouchInResizeArea(event.getX(), event.getY(), v.getWidth(), v.getHeight());
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (isResizing) {
                            // Handle resizing
                            int newWidth = initialWidth + (int)(event.getRawX() - initialTouchX);
                            int newHeight = initialHeight + (int)(event.getRawY() - initialTouchY);
                            
                            // Ensure minimum size
                            params.width = Math.max(200, newWidth);
                            params.height = Math.max(150, newHeight);
                            
                            windowManager.updateViewLayout(view, params);
                        } else {
                            // Handle moving
                            params.x += (int)(event.getRawX() - initialTouchX);
                            params.y += (int)(event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(view, params);
                            
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        isResizing = false;
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
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        
        currentSizeState = (currentSizeState + 1) % 4;
        
        switch (currentSizeState) {
            case 0: // Small size
                params.width = screenWidth / 2;
                params.height = screenHeight / 3;
                break;
            case 1: // Medium size
                params.width = (int)(screenWidth * 0.75);
                params.height = screenHeight / 2;
                break;
            case 2: // Large size
                params.width = screenWidth;
                params.height = (int)(screenHeight * 0.75);
                break;
            case 3: // Full screen
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                break;
        }
        
        windowManager.updateViewLayout(linearLayout1, params);
        isFullScreen = (currentSizeState == 3);
    }

    private boolean isTouchInResizeArea(float x, float y, int viewWidth, int viewHeight) {
        float resizeHandleSize = RESIZE_HANDLE_SIZE * getResources().getDisplayMetrics().density;
        return x >= viewWidth - resizeHandleSize && y >= viewHeight - resizeHandleSize;
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
