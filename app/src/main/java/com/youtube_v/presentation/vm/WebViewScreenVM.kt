package com.youtube_v.presentation.vm

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.youtube_v.domain.JavaScriptExecutor
import com.youtube_v.domain.SavingManager
import com.youtube_v.domain.OpenFloatingActivity
import com.youtube_v.domain.SkipVideoTime
import com.youtube_v.domain.core.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewScreenVM @Inject constructor(
    val prefs: SharedPreferences
) : ViewModel() {
    lateinit var skipVideoTime: SkipVideoTime

    var speedPlaybackVideo = mutableStateOf(1f)

    fun initializePlaybackSpeed() {
        var speed = prefs.getFloat(AppConstants.PREF_SPEED, 1f)
        speedPlaybackVideo.value = speed
    }

    fun setVideoSpeed(webView: WebView) {
        var speed = prefs.getFloat(AppConstants.PREF_SPEED, 1f)

        speed = if (speed >= 3f) 1f else speed + 1f
        SavingManager.savePlayBackSpeed(prefs, speed)
        speedPlaybackVideo.value = speed
        JavaScriptExecutor.applyPlaybackSpeed(speed, webView)
    }

    fun skipVideo(context: Context, webView: WebView) {
        skipVideoTime =
            SkipVideoTime(context, webView)
        skipVideoTime.showSkipTimeDialog()
    }

    fun openFloatingWindow(context: Context, activity: Activity, webView: WebView) {
        OpenFloatingActivity.checkOverlayPermission(context, activity, webView)

    }

    fun subtitleMakeOf(webView: WebView) {
        JavaScriptExecutor.makeSubtitleOf(webView)
    }

    var cycleVideo = mutableStateOf(false)
        private set

    fun videoCycling(webView: WebView) {
        cycleVideo.value = !cycleVideo.value
        JavaScriptExecutor.cyclingVideo(webView, cycleVideo.value)
    }
}