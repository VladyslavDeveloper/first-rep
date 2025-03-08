package com.example.myyoutube;
import android.os.PowerManager;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

public class FloatingActivity extends AppCompatActivity {
    private LinearLayout linearLayout1;
    private WebView webView;
    private LinearLayout sizeControlLayout;
    private SeekBar sizeSeekBar;
    private boolean isSizeControlVisible = false;
    private Button btnDownload;

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
    private static final int STATIC_WIDTH = 400; // Static width in dp
    private static final float DEFAULT_HEIGHT_PERCENT = 0.4f; // 40% of screen height
    
    // Touch handling variables
    private float initialTouchX;
    private float initialTouchY;
    private int initialWidth;
    private int initialHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "MyApp::MyWakelockTag");
        wakeLock.acquire();


        // Initialize WindowManager and LayoutParams
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        
        params = new WindowManager.LayoutParams(
                (int) (STATIC_WIDTH * getResources().getDisplayMetrics().density), // Convert dp to pixels
                (int) (screenHeight * DEFAULT_HEIGHT_PERCENT), // Set default height to 40% of screen
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
        sizeControlLayout = view.findViewById(R.id.sizeControlLayout);
        sizeSeekBar = view.findViewById(R.id.sizeSeekBar);
        btnDownload = view.findViewById(R.id.btnDownload);

        // Setup WebView
        initializeWebView();
        loadVideoUrlFromIntent();

        // Setup size control
        setupSizeControl();

        // Set touch listener for moving the window
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Handle moving
                        params.x += (int)(event.getRawX() - initialTouchX);
                        params.y += (int)(event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(view, params);
                        
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
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
        view.findViewById(R.id.btnVoiceSearch1).setOnClickListener(v -> startVoiceSearch());
        view.findViewById(R.id.btnDownload).setOnClickListener(v -> downloadCurrentVideo());

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
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        
        // Enable hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Inject JavaScript to keep video playing in background
                webView.evaluateJavascript(
                    "javascript:(function() {" +
                    "    var video = document.querySelector('video');" +
                    "    if(video) {" +
                    "        video.addEventListener('play', function() {" +
                    "            video.setAttribute('keepalive', 'true');" +
                    "        });" +
                    "    }" +
                    "})();", null
                );
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


    private void setupSizeControl() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float scale = Math.max(0.1f, progress / 100f);
                    params.height = (int) (screenHeight * scale);
                    windowManager.updateViewLayout(linearLayout1, params);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void toggleSizeControl() {
        isSizeControlVisible = !isSizeControlVisible;
        sizeControlLayout.setVisibility(isSizeControlVisible ? View.VISIBLE : View.GONE);
        
        // Update seek bar progress based on current height
        if (isSizeControlVisible) {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            float currentScale = (float) params.height / screenHeight;
            sizeSeekBar.setProgress((int) (currentScale * 100));
        }
    }

    private boolean isTouchInResizeArea(float x, float y, int viewWidth, int viewHeight) {
        return false; // Disable resize handle
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Go back to the previous page in WebView history
        } else {
            super.onBackPressed(); // Exit the activity if there's no history
        }
    }

    private void downloadCurrentVideo() {
        webView.evaluateJavascript(
            "(function() {" +
            "    try {" +
            "        var videoElement = document.querySelector('video');" +
            "        var sources = [];" +
            "        if (videoElement) {" +
            "            sources.push(videoElement.src);" +
            "            var sourceTags = videoElement.getElementsByTagName('source');" +
            "            for (var i = 0; i < sourceTags.length; i++) {" +
            "                sources.push(sourceTags[i].src);" +
            "            }" +
            "        }" +
            "        // Try to get YouTube URL" +
            "        var ytPlayer = document.querySelector('.html5-main-video');" +
            "        if (ytPlayer) {" +
            "            sources.push(ytPlayer.src);" +
            "        }" +
            "        // Get all video sources from the page" +
            "        var allVideos = document.getElementsByTagName('video');" +
            "        for (var i = 0; i < allVideos.length; i++) {" +
            "            if (allVideos[i].src) sources.push(allVideos[i].src);" +
            "        }" +
            "        return JSON.stringify(sources);" +
            "    } catch (e) {" +
            "        return '';" +
            "    }" +
            "})();",
            new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    try {
                        if (value != null && !value.equals("null") && !value.equals("[]")) {
                            // Remove quotes from JSON string
                            value = value.replace("\"", "");
                            value = value.replace("[", "");
                            value = value.replace("]", "");
                            
                            // Split by comma if multiple sources
                            String[] urls = value.split(",");
                            boolean started = false;
                            
                            // Try each URL
                            for (String url : urls) {
                                if (!url.isEmpty() && url.startsWith("http")) {
                                    startDownload(url);
                                    started = true;
                                    break;
                                }
                            }
                            
                            if (!started) {
                                showDownloadError();
                            }
                        } else {
                            showDownloadError();
                        }
                    } catch (Exception e) {
                        showDownloadError();
                    }
                }
            }
        );
    }

    private void showDownloadError() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Download Error")
                   .setMessage("Cannot download this video directly. You may need to use a different method or check if the video is downloadable.")
                   .setPositiveButton("OK", null)
                   .show();
        });
    }

    private void startDownload(String videoUrl) {
        try {
            if (!videoUrl.contains("googlevideo.com") && !videoUrl.contains("youtube.com")) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
                String fileName = "video_" + System.currentTimeMillis() + ".mp4";
                request.setTitle("Downloading Video");
                request.setDescription("Downloading video");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                request.allowScanningByMediaScanner();
                request.setAllowedOverMetered(true);
                request.setAllowedOverRoaming(true);

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    downloadManager.enqueue(request);
                    Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
                }
            } else {
                showDownloadError();
            }
        } catch (Exception e) {
            showDownloadError();
        }
    }

}
