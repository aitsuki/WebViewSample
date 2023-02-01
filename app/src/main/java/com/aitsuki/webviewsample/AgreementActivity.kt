package com.aitsuki.webviewsample

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.aitsuki.webviewsample.browser.AppBrowser
import com.aitsuki.webviewsample.browser.UICallback
import com.aitsuki.webviewsample.browser.UrlRouter
import com.aitsuki.webviewsample.databinding.ActivityAgreementBinding

class AgreementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgreementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val browser = AppBrowser(binding.webContainer)
        browser.setUICallback(object : UICallback {
            override fun onReceivedTitle(webView: WebView, title: String) {
                binding.toolbar.title = title
            }

            override fun onLoadSuccess() {
                binding.bottomLayout.isVisible = true
            }

            override fun onContentReachToBottom() {
                binding.agreeButton.isEnabled = true
            }
        })
        browser.setUrlRouter(object : UrlRouter {
            // reject non-http request
            override fun route(url: String): Boolean {
                return !url.startsWith("http")
            }
        })
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.agreeButton.setOnClickListener { finish() }
        browser.loadUrl("https://www.bilibili.com/blackboard/account-useragreement.html")
    }
}