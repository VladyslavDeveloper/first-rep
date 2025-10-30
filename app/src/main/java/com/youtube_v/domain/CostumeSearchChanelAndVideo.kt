package com.youtube_v.domain

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import javax.inject.Inject

class CostumeSearchChanelAndVideo @Inject constructor(){
    fun showSearchDialog(context: Context?, webView: WebView) {
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setTitle("Search youtube channel or RecentVideos")
        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        input.setLayoutParams(lp)
        builder.setView(input)
        builder.setPositiveButton("Search") { dialog, which ->
            val query = input.getText().toString().trim { it <= ' ' }
            if (!query.isEmpty()) {
                searchRecentVideos(query, webView)
            }
        }
        builder.setNegativeButton("Search Channel") { dialog, which ->
            val query = input.getText().toString().trim { it <= ' ' }
            if (!query.isEmpty()) {
                searchChannel(query, webView)
            }
        }
        builder.show()
    }

    fun searchChannel(query: String?, webView: WebView) {
        // Construct the YouTube search URL with recent filter
        val url =
            "https://www.youtube.com/results?search_query=" + Uri.encode(query) + "&sp=CAESAhAC"
        webView.loadUrl(url)
    }

    fun searchRecentVideos(query: String?, webView: WebView) {
        // Конструируем URL с фильтром для видео
        val url =
            "https://www.youtube.com/results?search_query=" + Uri.encode(query) + "&sp=EgIQAQ%253D%253D"
        webView.loadUrl(url)
    }
}
