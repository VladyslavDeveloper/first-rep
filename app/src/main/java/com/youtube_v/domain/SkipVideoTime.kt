package com.youtube_v.domain

import android.content.Context
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import javax.inject.Inject

class SkipVideoTime @Inject constructor() {
    fun showSkipTimeDialog(context: Context, webView: WebView) {
        val skipOptions = arrayOf("3 Minutes", "5 Minutes", "10 Minutes", "15 Minutes")
        val builder = AlertDialog.Builder(
            context
        )
        builder.setTitle("Select Skip Time")
        builder.setItems(skipOptions) { dialog, which ->
            var skipTime = 0
            when (which) {
                0 -> skipTime = 180
                1 -> skipTime = 300
                2 -> skipTime = 600
                3 -> skipTime = 900
            }
            webView.evaluateJavascript(
                "document.querySelector('video').currentTime += $skipTime;",
                null
            )
        }
        builder.show()
    }

    fun skipThreeMinutes(webView: WebView) {
        webView.evaluateJavascript("document.querySelector('video').currentTime += 60;", null)
    }
}
