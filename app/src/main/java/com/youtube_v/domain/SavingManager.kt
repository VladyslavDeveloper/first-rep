package com.youtube_v.domain

import android.content.Context
import android.content.SharedPreferences
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.youtube_v.domain.core.AppConstants
import javax.inject.Inject

class SavingManager @Inject constructor(
    val javaScriptExecutor: JavaScriptExecutor
){
    fun initializeWebView(webView: WebView, context: Context) {
        val webSettings = webView.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        // Enable cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                javaScriptExecutor.applyPlaybackSpeed(1f, webView)
                javaScriptExecutor.makeSubtitleOf(webView)
            }
        })
        loadSavedURL(context, webView)
    }

    fun saveLastVideoUrl(url: String?, context: Context) {
        val preferences =
            context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(AppConstants.PREF_URL, url)
        editor.apply()
    }

    private fun loadSavedURL(context: Context, webView: WebView) {
        // Load the last saved URL
        val preferences =
            context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val lastVideoUrl = preferences.getString(AppConstants.PREF_URL, AppConstants.BASE_URL)
        webView.loadUrl(lastVideoUrl!!)
    }

    fun savePlayBackSpeed(prefs: SharedPreferences, speed: Float) {
        prefs.edit()
            .putFloat(AppConstants.PREF_SPEED, speed)
            .apply()
    }

    fun loadPlayBackSpeed(context: Context, webView: WebView?) {
        // Load the last playback speed
        val preferences =
            context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val playbackSpeed = preferences.getFloat(AppConstants.PREF_SPEED, 1.0f)
        javaScriptExecutor.applyPlaybackSpeed(playbackSpeed, webView!!)
    }
}
