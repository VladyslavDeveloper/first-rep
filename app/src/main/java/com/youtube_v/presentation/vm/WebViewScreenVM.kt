package com.youtube_v.presentation.vm

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.youtube_v.domain.CostumeSearchChanelAndVideo
import com.youtube_v.domain.JavaScriptExecutor
import com.youtube_v.domain.SavingManager
import com.youtube_v.utils.OpenFloatingActivity
import com.youtube_v.domain.SkipVideoTime
import com.youtube_v.domain.TimerExecution
import com.youtube_v.domain.core.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewScreenVM @Inject constructor(
    private val savingManager: SavingManager,
    val timerExecution: TimerExecution,
    val skipVideoTime: SkipVideoTime,
    val javaScriptExecutor: JavaScriptExecutor,
    val costumeSearchChanelAndVideo: CostumeSearchChanelAndVideo,
    val prefs: SharedPreferences
) : ViewModel() {

    var speedPlaybackVideo = mutableStateOf(1f)

    fun initializeContent(webView: WebView, context: Context) {
        var speed = prefs.getFloat(AppConstants.PREF_SPEED, 1f)
        speedPlaybackVideo.value = speed
        savingManager.initializeWebView(webView, context)
        timerExecution.startDurationCheck(webView, context)
    }

    fun setVideoSpeed(webView: WebView) {
        var speed = prefs.getFloat(AppConstants.PREF_SPEED, 1f)

        speed = if (speed >= 3f) 1f else speed + 1f
        savingManager.savePlayBackSpeed(prefs, speed)
        speedPlaybackVideo.value = speed
        javaScriptExecutor.applyPlaybackSpeed(speed, webView)
    }

    fun skipVideo(context: Context, webView: WebView) {
        skipVideoTime.showSkipTimeDialog(context, webView)
    }

    fun openFloatingWindow(context: Context, activity: Activity) {
        OpenFloatingActivity.checkOverlayPermission(context, activity)
    }

    fun fullScreenVideo(webView: WebView, isLandscape: Boolean) {
        if (isLandscape) {
            javaScriptExecutor.videoFullScreen(webView)
        } else {
            javaScriptExecutor.videoFullScreenCancel(webView)
        }
    }

    fun subtitleMakeOf(webView: WebView) {
        javaScriptExecutor.makeSubtitleOf(webView)
    }

    var cycleVideo = mutableStateOf(false)
        private set

    fun videoCycling(webView: WebView) {
        cycleVideo.value = !cycleVideo.value
        javaScriptExecutor.cyclingVideo(webView, cycleVideo.value)
    }

    fun showSearchDialog(context: Context, webView: WebView) {
        costumeSearchChanelAndVideo.showSearchDialog(context, webView)
    }
}