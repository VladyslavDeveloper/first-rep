package com.example.myyoutube;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;

public class VoiceSearch {
    private final Activity activity;
    private final WebView webView;
    public static final int VOICE_SEARCH_REQUEST_CODE = 1000;

    public VoiceSearch(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    public void startVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to search on YouTube");

        try {
            activity.startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {

        }
    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String query = results.get(0);
                String searchUrl = "https://www.youtube.com/results?search_query=" + query;
                webView.loadUrl(searchUrl);
            } else {

            }
        }
    }

}
