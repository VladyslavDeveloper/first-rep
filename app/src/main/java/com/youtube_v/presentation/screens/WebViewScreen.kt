package com.youtube_v.presentation.screens

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import com.youtube_v.domain.myyoutube.SpeedPlayback
import com.youtube_v.domain.myyoutube.TimerExecution
import com.youtube_v.presentation.vm.WebViewVM

@Composable
fun WebViewScreen(
    viewModel: WebViewVM,
    onBack: () -> Unit
) {
    val url = viewModel.url
    var webViewRef: WebView? = null

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewRef = this
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    loadUrl(url)

                }
            },
            update = { webView ->
                webView.loadUrl(url)
            },
            modifier = Modifier
                .weight(1f)
        )
        LazyRow(
            modifier = Modifier
                .heightIn(27.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {


            item {
                Button(onClick = {
                    SpeedPlayback.applyPlaybackSpeed(4f, webViewRef)
                }) {
                    Text("play X4")
                }
            }
        }
    }
}