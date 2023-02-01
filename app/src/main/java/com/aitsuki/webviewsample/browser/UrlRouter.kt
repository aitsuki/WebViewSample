package com.aitsuki.webviewsample.browser

interface UrlRouter {
    /**
     * @return true to consume this route
     */
    fun route(url: String): Boolean
}