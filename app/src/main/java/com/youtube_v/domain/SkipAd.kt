package com.youtube_v.domain

import android.content.ContentValues
import android.util.Log
import android.webkit.WebView
import javax.inject.Inject

class SkipAd @Inject constructor() {
    fun checkIfVideoExists(webView: WebView) {
        webView.evaluateJavascript(
            "(function() {" +
                    "var video = document.querySelector('video');" +
                    "if (video) {" +
                    "  return video.duration;" +
                    "} else {" +
                    "  return 0;" +
                    "}" +
                    "})();"
        ) { value ->
            try {
                val duration = value.toDouble()
                if (duration > 0) {
                    // Only if a valid video is present, attempt to skip ads
                    skipAds(webView)
                    checkVideoDurationAndSkipShort(webView)
                }
            } catch (e: NumberFormatException) {
                Log.e(ContentValues.TAG, "Error parsing video duration: " + e.message)
            }
        }
    }

    private fun checkVideoDurationAndSkipShort(webView: WebView) {
        webView.evaluateJavascript(
            "(function() {" +
                    "var video = document.querySelector('video');" +
                    "if (video) {" +
                    "return video.duration;" +
                    "} else {" +
                    "return 0;" +
                    "}" +
                    "})();"
        ) { value ->
            try {
                val duration = value.toDouble()
                if (duration < 25) { // If video is shorter than 4 seconds
                    // Skip forward 3 minutes (180 seconds)
                    webView.evaluateJavascript(
                        "var video = document.querySelector('video');" +
                                "if (video) {" +
                                "var newTime = video.currentTime + 180;" +
                                "if (newTime < video.duration) {" +
                                "video.currentTime = newTime;" +
                                "} else {" +
                                "video.currentTime = video.duration;" +
                                "}" +
                                "}", null
                    )
                } else {
                }
            } catch (e: NumberFormatException) {
                Log.e(ContentValues.TAG, "Error parsing video duration: " + e.message)
            }
        }
    }

    private fun skipAds(webView: WebView) {
        // Check and skip ads (if applicable)
        webView.evaluateJavascript(
            "javascript:(function() {" +
                    "const skipButton = document.querySelector('button[class*=\"ytp-ad-skip-button\"]');" +
                    "if (skipButton) {" +
                    "skipButton.click();" +  // Clicking the skip ad button
                    "const video = document.querySelector('video');" +
                    "if (video) {" +
                    "video.currentTime += 1900; " +  // Skip forward by 40 seconds
                    "return 'Реклама пропущена, видео перемотано на 1900 секунд';" +
                    "} else {" +
                    "return 'Видео не найдено, но реклама пропущена';" +
                    "}" +
                    "} else {" +
                    "return 'Кнопка пропуска рекламы не найдена';" +
                    "}" +
                    "})();"
        ) { value: String? ->
            if (value != null && value != "null") {
            }
        }
    }
}
