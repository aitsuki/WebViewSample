package com.aitsuki.webviewsample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aitsuki.webviewsample.browser.AppBrowser
import com.aitsuki.webviewsample.browser.UICallback
import com.aitsuki.webviewsample.browser.UrlRouter
import com.aitsuki.webviewsample.databinding.ActivityBrowserBinding
import kotlin.concurrent.thread

class BrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrowserBinding
    private lateinit var browser: AppBrowser

    private val initUrl get() = intent.getStringExtra("url") ?: error("must be provide a url")

    private val pickPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                onPickPhotoResult(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        browser = AppBrowser(binding.webContainer)
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
        browser.handleOnBackPressed(onBackPressedDispatcher)
        browser.addJsInterface("Android", AndroidInterface())
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
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

    private fun onPickPhotoResult(uri: Uri) {
        thread {
            val base64Image = contentResolver.openInputStream(uri)?.use { input ->
                Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
            }
            if (base64Image != null) {
                runOnUiThread {
                    browser.callJs("onPickPhotoResult", "'$base64Image'")
                }
            }
        }
    }

    /**
     * JavascriptInterface 注解的方法都是在子线程中运行
     */
    inner class AndroidInterface {

        @JavascriptInterface
        fun showToast(msg: String) {
            Toast.makeText(this@BrowserActivity, msg, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun getAppVersion(): String {
            return BuildConfig.VERSION_NAME
        }

        @JavascriptInterface
        fun pickPhoto() {
            pickPhotoLauncher.launch("image/*")
        }
    }
}