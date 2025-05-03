package com.example.myyoutube;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.webkit.WebView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class VoiceSearch {
    public static final int VOICE_SEARCH_REQUEST_CODE = 1000;

    // Старт голосового ввода
    public static void startVoiceSearch(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to search on YouTube");

        try {
            activity.startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Voice search not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    // Обработка результата
    public static void handleResult(int requestCode, int resultCode, Intent data, Activity activity, WebView webView) {
        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                openYouTubeSearch(results.get(0), webView);
            } else {
                Toast.makeText(activity, "No voice input detected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void openYouTubeSearch(String query, WebView webView) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String searchUrl = "https://www.youtube.com/results?search_query=" + encodedQuery;
            webView.loadUrl(searchUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
