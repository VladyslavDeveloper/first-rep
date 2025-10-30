package com.youtube_v.domain

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.webkit.WebView
import android.widget.Toast
import com.youtube_v.domain.core.AppConstants
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

class VoiceSearch @Inject constructor(){
    // Старт голосового ввода
    fun startVoiceSearch(activity: Activity) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to search on YouTube")
        try {
            activity.startActivityForResult(intent, AppConstants.VOICE_SEARCH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                "Voice search not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Обработка результата
    fun handleResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        activity: Activity?,
        webView: WebView
    ) {
        if (requestCode == AppConstants.VOICE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && !results.isEmpty()) {
                openYouTubeSearch(results[0], webView)
            } else {
                Toast.makeText(activity, "No voice input detected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openYouTubeSearch(query: String, webView: WebView) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "https://www.youtube.com/results?search_query=$encodedQuery"
            webView.loadUrl(searchUrl)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
}
