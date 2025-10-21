package com.youtube_v.presentation.vm

import android.webkit.WebView
import androidx.lifecycle.ViewModel
import com.youtube_v.domain.myyoutube.SpeedPlayback
import com.youtube_v.domain.myyoutube.TimerExecution

class WebViewVM : ViewModel() {
    val url = "https://www.youtube.com"

    fun setVideoSpeed(webView: WebView){
        SpeedPlayback.applyPlaybackSpeed(4f, webView)
    }
}