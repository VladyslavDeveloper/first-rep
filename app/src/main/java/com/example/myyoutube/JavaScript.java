package com.example.myyoutube;



import android.webkit.WebView;

public class JavaScript {
    public static void videoFullScreen(WebView webView) {
        webView.evaluateJavascript(
                "(function() { " +
                        // Ваш первый скрипт
                        "var videos = document.querySelectorAll('video');" +
                        "for (var i = 0; i < document.body.children.length; i++) {" +
                        "var el = document.body.children[i];" +
                        "if (!el.querySelector('video')) { el.style.display = 'none'; }" +
                        "}" +
                        "document.documentElement.style.overflow = 'auto';" + // Разрешает прокрутку, если она необходима

                        "document.body.style.margin = '0';" +
                        "document.body.style.padding = '0';" +


                        "videos.forEach(function(video) {" +
                        "video.style.position = 'fixed';" +
                        "video.style.top = '20';" +
                        "video.style.left = '8';" +
                        "video.style.width = '80vw';" +
                        "video.style.height = '100vh';" +
                        "video.style.zIndex = '9999';" +
                        "video.style.objectFit = 'cover';" +
                        "});" +

                        // Ваш второй скрипт для скрытия верхней панели
                        "var topBar = document.querySelector('ytd-masthead');" +
                        "if (topBar) { " +  // Если панель найдена
                        "topBar.style.display = 'none';" +  // Скрываем панель
                        "}" +

                        "})();",
                null
                // this method make current video on the full screen(delete all except video container)
        );

    }


    public static void videoFullScreenCancel(WebView webView) {
        webView.evaluateJavascript(
                "(function() {" +
                        "var videos = document.querySelectorAll('video');" +
                        "videos.forEach(function(video) {" +
                        "video.style = '';" + // Убираем стили
                        "});" +
                        "for (var i = 0; i < document.body.children.length; i++) {" +
                        "document.body.children[i].style.display = '';" +
                        "}" +
                        "var topBar = document.querySelector('ytd-masthead');" +
                        "if (topBar) { topBar.style.display = ''; }" +
                        "document.documentElement.style.overflow = '';" +
                        "document.body.style.margin = '';" +
                        "document.body.style.padding = '';" +
                        "})();",
                null
        );
        // Cancel full screen mode
    }


    public static void makeSubtitleOf(WebView webView) {
        webView.evaluateJavascript(
                "function disableSubtitles() {" +
                        // Method 1: Direct HTML5 video track disabling
                        "  var video = document.querySelector('video');" +
                        "  if(video && video.textTracks) {" +
                        "    for(var i = 0; i < video.textTracks.length; i++) {" +
                        "      video.textTracks[i].mode = 'disabled';" +
                        "    }" +
                        "  }" +
                        // Method 2: YouTube specific button
                        "  var ccButton = document.querySelector('.ytp-subtitles-button');" +
                        "  if(ccButton && ccButton.getAttribute('aria-pressed') === 'true') {" +
                        "    ccButton.click();" +
                        "  }" +
                        // Method 3: YouTube settings menu
                        "  var subtitlesMenuItem = document.querySelector('[role=\"menuitem\"][aria-label*=\"subtitles\"]');" +
                        "  if(subtitlesMenuItem) {" +
                        "    subtitlesMenuItem.click();" +
                        "  }" +
                        // Method 4: Remove caption elements
                        "  var captionWindow = document.querySelector('.ytp-caption-window-container');" +
                        "  if(captionWindow) {" +
                        "    captionWindow.style.display = 'none';" +
                        "  }" +
                        "}" +
                        // Run immediately
                        "disableSubtitles();" +
                        // Run periodically
                        "setInterval(disableSubtitles, 500);" +
                        // Also run when video source changes
                        "if(video) {" +
                        "  video.addEventListener('loadeddata', disableSubtitles);" +
                        "}",
                null
        );
        //make subtitle off on the video
    }


    public static void makeJoystick(WebView webView, int numberMethods, float xPercent) {

        final float REWIND_MULTIPLIER = 10.0f;
        if (numberMethods == 1) {
            webView.evaluateJavascript(
                    "var video = document.querySelector('video');" +
                            "if(video) { video.currentTime = video.currentTime + (window.joystickSeekValue || 0); }",
                    null
            );
            // fast forward
        } else if (numberMethods == 2) {
            // X-axis controls seeking speed
            float seekValue = xPercent * REWIND_MULTIPLIER;

            webView.evaluateJavascript(
                    "window.joystickSeekValue = " + seekValue + ";" +
                            "var video = document.querySelector('video');" +
                            "if(video) {" +
                            "  video.playbackRate = " + (Math.abs(xPercent) < 0.1 ? "1.0" : "0.0") + ";" +
                            "}",
                    null
            );
            // rewind video

        } else if (numberMethods == 3) {
            webView.evaluateJavascript(
                    "window.joystickSeekValue = 0;" +
                            "var video = document.querySelector('video');" +
                            "if(video) { video.playbackRate = 1.0; }",
                    null
            );
            // stop joystick
        }
    }
}
