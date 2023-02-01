package com.aitsuki.webviewsample.browser

import android.webkit.WebView

interface UICallback {
    fun onReceivedTitle(webView: WebView, title: String) {}

    fun onContentReachToBottom() {}

    /**
     * progress is 100 without error
     */
    fun onLoadSuccess() {}
}