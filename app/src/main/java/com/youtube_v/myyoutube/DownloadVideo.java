package com.youtube_v.myyoutube;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.widget.Toast;

public class DownloadVideo {

    public static void loadVideoInPlayer(String videoUrl, WebView webView) {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            if (videoUrl.contains("googlevideo.com/videoplayback")) {
                // Extract title from URL parameters
                String title = "";
                if (videoUrl.contains("title=")) {
                    try {
                        String[] params = videoUrl.split("&");
                        for (String param : params) {
                            if (param.startsWith("title=")) {
                                title = param.substring(6).replace("+", " ");
                                title = java.net.URLDecoder.decode(title, "UTF-8");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        title = "Video";
                    }
                }

                // For direct video URLs, create a custom HTML page with video player
                String customHtml = "<html><body style='margin:0; padding:0; background:black;'>" +
                        "<div style='color:white; padding:10px; font-family:Arial;'>" + title + "</div>" +
                        "<video style='width:100%; height:calc(100% - 40px);' controls autoplay>" +
                        "<source src='" + videoUrl + "' type='video/mp4'>" +
                        "</video></body></html>";
                webView.loadData(customHtml, "text/html", "UTF-8");
            } else {
                webView.loadUrl(videoUrl);
            }
        } else {
            webView.loadUrl("https://www.youtube.com");
        }
    }
    public static void downloadCurrentVideo(Activity activity, WebView webView) {
        String currentUrl = activity.getIntent().getStringExtra("video_url");

        if (currentUrl != null && currentUrl.contains("googlevideo.com/videoplayback")) {
            DownloadVideo.loadVideoInPlayer(currentUrl,webView);
            Toast.makeText(activity, "Loading video...", Toast.LENGTH_SHORT).show();
        } else {
            try {
                // Encode the video URL
                String encodedUrl = java.net.URLEncoder.encode(currentUrl, "UTF-8");

                // Construct the SaveFrom.net URL with the video link
                String saveFromUrl = "https://uk.savefrom.net/1-youtube-video-downloader.html?url=" + encodedUrl;

                // Open the URL in the browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(saveFromUrl));
                activity.startActivity(intent);

                Toast.makeText(activity, "Opening SaveFrom.net in the browser...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, "Error opening URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
