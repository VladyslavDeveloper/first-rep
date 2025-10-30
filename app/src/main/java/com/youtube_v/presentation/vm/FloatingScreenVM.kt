package com.youtube_v.presentation.vm

import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import com.youtube_v.domain.SavingManager
import com.youtube_v.domain.SkipVideoTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FloatingScreenVM @Inject constructor(
    val savingManager: SavingManager,
    val skipVideoTime: SkipVideoTime
) : ViewModel() {
    fun initializeContent(webView: WebView, context: Context){
        // Setup WebView
        savingManager.initializeWebView(webView, context)
    }

    fun skipVideoTime(webView: WebView){
        skipVideoTime.skipThreeMinutes(webView)
    }
}