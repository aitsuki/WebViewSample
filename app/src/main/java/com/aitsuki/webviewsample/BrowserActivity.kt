package com.aitsuki.webviewsample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.aitsuki.webviewsample.browser.AppBrowser
import com.aitsuki.webviewsample.browser.UICallback
import com.aitsuki.webviewsample.browser.UrlRouter
import com.aitsuki.webviewsample.databinding.ActivityBrowserBinding

class BrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrowserBinding

    private val initUrl get() = intent.getStringExtra("url") ?: error("must be provide a url")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val browser = AppBrowser(binding.webContainer)
        browser.setUICallback(object : UICallback {
            override fun onReceivedTitle(webView: WebView, title: String) {
                binding.toolbar.title = title
            }
        })
        browser.setUrlRouter(object : UrlRouter {
            override fun route(url: String): Boolean {
                if (url.startsWith("http")) return false
                openActionView(url)
                return true
            }
        })
        browser.addJsInterface("Android", BrowserBridge(this))
        binding.toolbar.setNavigationOnClickListener { finish() }
        browser.loadUrl(initUrl)
    }

    private fun openActionView(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}