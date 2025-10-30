package com.youtube_v.presentation.vm

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.youtube_v.domain.myyoutube.JavaScript
import com.youtube_v.domain.use_cases.OpenFloatingActivity
import com.youtube_v.domain.use_cases.ShowSkipDialog
import com.youtube_v.domain.myyoutube.SpeedPlayback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewScreenVM @Inject constructor(
    val prefs: SharedPreferences
) : ViewModel() {
    val url = "https://www.youtube.com"
    lateinit var showSkipDialog: ShowSkipDialog

    var speedPlaybackVideo = mutableStateOf(1f)

    fun initializePlaybackSpeed(context: Context) {
        var speed = prefs.getFloat("playback_speed", 1f)
        speedPlaybackVideo.value = speed
    }

    fun setVideoSpeed(webView: WebView, context: Context) {
        var speed = prefs.getFloat("playback_speed", 1f)

        speed = if (speed >= 3f) 1f else speed + 1f
        prefs.edit()
            .putFloat("playback_speed", speed)
            .apply()

        speedPlaybackVideo.value = speed
        SpeedPlayback.applyPlaybackSpeed(speed, webView)
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