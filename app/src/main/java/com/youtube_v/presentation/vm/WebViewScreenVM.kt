package com.youtube_v.presentation.vm

import android.app.Activity
import android.content.Context
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.youtube_v.domain.myyoutube.JavaScript
import com.youtube_v.domain.use_cases.OpenFloatingActivity
import com.youtube_v.domain.use_cases.ShowSkipDialog
import com.youtube_v.domain.myyoutube.SpeedPlayback
import com.youtube_v.domain.myyoutube.VoiceSearch

class WebViewScreenVM : ViewModel() {
    val url = "https://www.youtube.com"
    lateinit var showSkipDialog: ShowSkipDialog
    var speedPlaybackVideo = mutableStateOf(1f)

    fun setVideoSpeed(webView: WebView) {
        speedPlaybackVideo.value = (speedPlaybackVideo.value % 4f) + 1f
        SpeedPlayback.applyPlaybackSpeed(speedPlaybackVideo.value, webView)
    }

    fun skipVideo(context: Context, webView: WebView) {
        showSkipDialog =
            ShowSkipDialog(context, webView)
        showSkipDialog.showSkipTimeDialog()
    }

    fun openFloatingWindow(context: Context, activity: Activity, webView: WebView) {
        OpenFloatingActivity.checkOverlayPermission(context, activity, webView)

    }

    fun subtitleMakeOf(webView: WebView) {
        JavaScript.makeSubtitleOf(webView)
    }

    var cycleVideo = mutableStateOf(false)
        private set

    fun videoCycling(webView: WebView) {
        cycleVideo.value = !cycleVideo.value
        webView.evaluateJavascript(
            "document.querySelector('video').loop = " + cycleVideo + ";",
            null
        )
    }
}