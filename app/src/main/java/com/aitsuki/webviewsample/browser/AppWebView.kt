package com.aitsuki.webviewsample.browser

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class AppWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs) {

    private var listener: (() -> Unit)? = null

    fun setOnContentReachToBottomListener(listener: () -> Unit) {
        this.listener = listener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (!canScrollVertically(1)) {
            listener?.invoke()
        }
    }
}