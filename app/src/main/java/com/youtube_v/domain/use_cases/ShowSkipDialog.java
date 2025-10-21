package com.youtube_v.domain.use_cases;

import android.content.Context;
import android.content.DialogInterface;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;

public class ShowSkipDialog {
    private Context context;
    private WebView webView;

    // Constructor to receive context and WebView
    public ShowSkipDialog(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    public void showSkipTimeDialog() {
        String[] skipOptions = {"3 Minutes", "5 Minutes", "10 Minutes", "15 Minutes"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Skip Time");
        builder.setItems(skipOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int skipTime = 0;
                switch (which) {
                    case 0:
                        skipTime = 180;
                        break;
                    case 1:
                        skipTime = 300;
                        break;
                    case 2:
                        skipTime = 600;
                        break;
                    case 3:
                        skipTime = 900;
                        break;
                }
                webView.evaluateJavascript("document.querySelector('video').currentTime += " + skipTime + ";", null);

            }
        });
        builder.show();
    }
    public static void skipThreeMinutes(WebView webView) {
        webView.evaluateJavascript("document.querySelector('video').currentTime += 60;", null);
    }

}
