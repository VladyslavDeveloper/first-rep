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

class WebViewVM : ViewModel() {
    val url = "https://www.youtube.com"
    lateinit var showSkipDialog: ShowSkipDialog

    fun setVideoSpeed(webView: WebView){
        SpeedPlayback.applyPlaybackSpeed(4f, webView)
    }

    fun skipVideo (context: Context, webView: WebView){
        showSkipDialog =
            ShowSkipDialog(context, webView)
        showSkipDialog.showSkipTimeDialog()
    }

    fun openFloatingWindow(context: Context, activity: Activity, webView: WebView){
        OpenFloatingActivity.checkOverlayPermission(context, activity, webView)

    }

    fun subtitleMakeOf(webView: WebView){
        JavaScript.makeSubtitleOf(webView)
    }

    var cycleVideo = mutableStateOf(false)
        private set
    fun videoCycling(webView: WebView){
        cycleVideo.value = !cycleVideo.value
        webView.evaluateJavascript(
            "document.querySelector('video').loop = " + cycleVideo + ";",
            null
        )
    }
}