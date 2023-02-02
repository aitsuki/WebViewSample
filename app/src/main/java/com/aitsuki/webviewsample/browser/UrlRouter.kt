package com.aitsuki.webviewsample.browser

interface UrlRouter {
    /**
     * @return true to consume this url
     */
    fun route(url: String): Boolean
}