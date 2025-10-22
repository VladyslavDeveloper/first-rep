package com.youtube_v.presentation.screens.utils

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.net.URLEncoder

@Composable
fun VoiceSearchButton(webView: WebView) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                val query = results[0]
                webView.loadUrl(
                    "https://www.youtube.com/results?search_query=${
                        URLEncoder.encode(
                            query,
                            "UTF-8"
                        )
                    }"
                )
            }
        }
    }


    Button(onClick = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something...")
        launcher.launch(intent)
    }) {
        Text(text = "voice input..")

    }
}