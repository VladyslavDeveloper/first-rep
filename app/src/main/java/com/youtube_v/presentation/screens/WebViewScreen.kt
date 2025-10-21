package com.youtube_v.presentation.screens

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import com.youtube_v.presentation.vm.WebViewVM

@Composable
fun WebViewScreen(
    viewModel: WebViewVM,
    onBack: () -> Unit
) {
    val url = viewModel.url

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}