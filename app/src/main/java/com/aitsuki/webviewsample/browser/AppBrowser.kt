package com.aitsuki.webviewsample.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.aitsuki.webviewsample.R
import com.google.android.material.progressindicator.LinearProgressIndicator

class AppBrowser(container: FrameLayout) {

    companion object {
        private const val TAG = "AppBrowser"
    }

    private val webView: AppWebView
    private val errorView: View
    private val progressBar: ProgressBar
    private var urlRouter: UrlRouter? = null
    private var uiCallback: UICallback? = null

    init {
        webView = AppWebView(container.context)
        progressBar = LinearProgressIndicator(container.context)
        errorView = View.inflate(container.context, R.layout.app_browser_error_layout, null)
        errorView.findViewById<Button>(R.id.retry_button).setOnClickListener { reload() }
        errorView.isVisible = false
        container.addView(
            webView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        container.addView(
            errorView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        container.addView(
            progressBar, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        setWebViewSettings(webView)
        setBrowserClients(webView)
        webView.setOnContentReachToBottomListener {
            uiCallback?.onContentReachToBottom()
        }
    }

    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    private fun reload() {
        progressBar.isVisible = true
        webView.reload()
        hideError()
    }

    fun setUrlRouter(router: UrlRouter) {
        this.urlRouter = router
    }

    fun setUICallback(callback: UICallback) {
        this.uiCallback = callback
    }

    fun handleOnBackPressed(dispatcher: OnBackPressedDispatcher) {
        dispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    hideError()
                    webView.goBack()
                } else {
                    isEnabled = false
                    dispatcher.onBackPressed()
                }
            }
        })
    }

    @SuppressLint("JavascriptInterface")
    fun addJsInterface(name: String, obj: Any) {
        webView.addJavascriptInterface(obj, name)
    }

    /**
     * param 是 String 类型，如果 js 函数的入参是 String， 需要用单引号或双引号括起来。
     * 例如：callJs("showMessage", "'hello, world!'")
     *      callJs("showMessage", "\"hello, world!\"")
     */
    fun callJs(func: String, param: String, callback: ValueCallback<String?>? = null) {
        webView.evaluateJavascript("""javascript:$func($param)""", callback)
    }

    private fun showError() {
        webView.isInvisible = true
        errorView.isVisible = true
    }

    private fun hideError() {
        // 在 onPageFinished 才让 webview 重新显示，因为此时的webview是一个默认的错误页面，不应该让用户看到
        // webView.isVisible = true
        errorView.isVisible = false
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewSettings(webView: WebView) {
        webView.settings.apply {
            // 设置自适应屏幕
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = false
            displayZoomControls = false

            // 自动加载图片
            loadsImagesAutomatically = true
            blockNetworkImage = false

            // 允许http
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            // 允许js
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
        }
    }

    private fun setBrowserClients(webView: WebView) {
        webView.webViewClient = object : WebViewClient() {

            @Suppress("OVERRIDE_DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: $url")
                return urlRouter?.route(url) ?: false
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest,
                errorResponse: WebResourceResponse
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.d(
                    TAG,
                    "onReceivedHttpError: url = ${request.url}, code = ${errorResponse.statusCode}, message = ${
                        errorResponse.data?.readBytes()?.let { String(it) }
                    }"
                )
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                Log.e(
                    TAG,
                    "onReceivedError: code = $errorCode, desc = $description, url = $failingUrl"
                )
                showError()
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                Log.d(TAG, "onPageCommitVisible: url = $url")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "onPageStarted: url = $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished: url = $url")
                if (errorView.isGone) {
                    webView.isVisible = true
                    uiCallback?.onLoadSuccess()
                    Log.d(TAG, "onLoadSuccess")
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.d(TAG, "onProgressChanged $newProgress")
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.isVisible = false
                }
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                Log.d(TAG, "onReceivedTitle: $title")
                uiCallback?.onReceivedTitle(view, title)
            }
        }
    }
}