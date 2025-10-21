package com.youtube_v.domain.myyoutube;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

public class LookLastVideo {

    public static void showSearchChannel(Context context, WebView webView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Search youtube channel or RecentVideos");

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String query = input.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchRecentVideos(query, webView);
                }
            }
        });

        builder.setNegativeButton("Search Channel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String query = input.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchChannel(query,webView);
                }
            }
        });

        builder.show();
    }

    public static void searchChannel(String query, WebView webView) {
        // Construct the YouTube search URL with recent filter
        String url = "https://www.youtube.com/results?search_query=" + Uri.encode(query) + "&sp=CAESAhAC";
        webView.loadUrl(url);
    }
    public static void searchRecentVideos(String query, WebView webView) {
        // Конструируем URL с фильтром для видео
        String url = "https://www.youtube.com/results?search_query=" + Uri.encode(query) + "&sp=EgIQAQ%253D%253D";
        webView.loadUrl(url);
    }
}
