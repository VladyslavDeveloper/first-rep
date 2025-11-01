package com.youtube_v.presentation.screens

import android.app.Activity
import android.content.res.Configuration
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.youtube_v.domain.core.AppConstants
import com.youtube_v.presentation.screens.utils.ActionSpinner
import com.youtube_v.presentation.screens.utils.VoiceSearchButton
import com.youtube_v.presentation.vm.WebViewScreenVM

@Composable
fun WebViewScreen(
    viewModel: WebViewScreenVM,
    onBack: () -> Unit
) {
    //isLandsCape
    val configuration = LocalConfiguration.current
    var isLandscapeState by remember {
        mutableStateOf(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    // Обновляем состояние при смене ориентации
    LaunchedEffect(configuration.orientation) {
        isLandscapeState = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    // ✅ Keep WebView reference across recompositions
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

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

                    viewModel.initializeContent(webViewRef!!, context)
                }
            },
            update = { webView ->
                viewModel.fullScreenVideo(webViewRef!!, isLandscapeState)
            },
            modifier = Modifier
                .weight(1f)
        )

        if (!isLandscapeState) {
            LazyRow(
                modifier = Modifier
                    .heightIn(57.dp)
                    .fillMaxWidth()
                    .background(Color.Gray),
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

                    VoiceSearchButton(webView = webViewRef!!)

                    ActionSpinner(
                        viewModel = viewModel,
                        context = context,
                        activity = activity,
                        webViewRef = webViewRef,
                        cycleVideo = cycleVideo
                    )
                }
            }
        }
    }
}