package com.youtube_v.domain

import android.content.Context
import android.os.Handler
import android.webkit.WebView
import javax.inject.Inject

class TimerExecution @Inject constructor(
    val savingManager: SavingManager
) {
    var handler: Handler? = null
    private var skipAd: SkipAd? = null
    fun startDurationCheck(webView: WebView, context: Context?) {
        if (handler == null) {
            handler = Handler()
        }
        handler!!.postDelayed(object : Runnable {
            override fun run() {
                skipAd = SkipAd(webView)
                skipAd!!.checkIfVideoExists()
                savingManager.saveLastVideoUrl(webView.getUrl(), context!!)
                savingManager.loadPlayBackSpeed(context!!, webView)
                handler!!.postDelayed(this, 1000)
            }
        }, 1000)
    }
}
