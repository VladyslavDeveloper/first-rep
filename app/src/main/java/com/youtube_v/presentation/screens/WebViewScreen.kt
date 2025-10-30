package com.youtube_v.presentation.screens

import android.app.Activity
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.youtube_v.domain.myyoutube.LookLastVideo
import com.youtube_v.domain.myyoutube.SavingManager
import com.youtube_v.domain.myyoutube.core.AppConstants
import com.youtube_v.presentation.screens.utils.VoiceSearchButton
import com.youtube_v.presentation.vm.WebViewScreenVM

@Composable
fun WebViewScreen(
    viewModel: WebViewScreenVM,
    onBack: () -> Unit
) {

    // ✅ Keep WebView reference across recompositions
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    viewModel.initializePlaybackSpeed()

    var cycleVideo by viewModel.cycleVideo

    BackHandler {
        if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        } else {
            onBack()
        }
    }
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
                    loadUrl(AppConstants.BASE_URL)

                }
            },
            update = { webView ->
                SavingManager.initializeWebView(webView, context)
            },
            modifier = Modifier
                .weight(1f)
        )
        LazyRow(
            modifier = Modifier
                .heightIn(27.dp)
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {


            item {
                Button(onClick = {
                    viewModel.setVideoSpeed(webViewRef!!)
                }) {
                    Text("speed video: ${viewModel.speedPlaybackVideo.value}")
                }

                Button(onClick = {
                    viewModel.skipVideo(context, webViewRef!!)
                }) {
                    Text("skip video")
                }

                Button(onClick = {
                    viewModel.openFloatingWindow(context, activity, webViewRef!!)
                }) {
                    Text("open window")
                }

                Button(onClick = {
                    viewModel.videoCycling(webViewRef!!)
                }) {
                    Text("cycle video: ${if (cycleVideo) "on" else "of"}")
                }

                VoiceSearchButton(webView = webViewRef!!)

                Button(onClick = { viewModel.subtitleMakeOf(webViewRef!!) }) {
                    Text(text = "subtitle of")
                }

                Button(onClick = {
                    LookLastVideo.showSearchChannel(activity, webViewRef)
                }) {
                    Text(text = "look up chanel")
                }
            }
        }
    }


}