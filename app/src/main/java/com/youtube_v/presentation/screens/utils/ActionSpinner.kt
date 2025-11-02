package com.youtube_v.presentation.screens.utils

import android.app.Activity
import android.content.Context
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.youtube_v.presentation.vm.WebViewScreenVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionSpinner(
    viewModel: WebViewScreenVM,
    context: Context,
    activity: Activity,
    webViewRef: WebView?,
    cycleVideo: Boolean
) {
    val items = mapOf(
        "open window" to { viewModel.openFloatingWindow(context, activity) },
        "cycle video: ${if (cycleVideo) "on" else "off"}" to { viewModel.videoCycling(webViewRef!!) },
        "cancel ads banner" to {viewModel.cancelAdsBanner(webViewRef!!)},
        "subtitle off" to { viewModel.subtitleMakeOf(webViewRef!!) },
        "look up chanel" to { viewModel.showSearchDialog(context, webViewRef!!) }
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Select action") }

    Box {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("actions")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { (title, action) ->
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        selectedText = title
                        expanded = false
                        action()
                    }
                )
            }
        }
    }
}