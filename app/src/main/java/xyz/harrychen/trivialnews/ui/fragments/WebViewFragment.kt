package xyz.harrychen.trivialnews.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import xyz.harrychen.trivialnews.support.utils.NetworkUtils
import xyz.harrychen.trivialnews.support.utils.NewsWebViewClient


class WebViewFragment : Fragment() {

    private var mWebView: WebView? = null
    private var mIsWebViewAvailable: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (mWebView != null) {
            mWebView!!.destroy()
        }
        mWebView = WebView(activity)

        val url = arguments!!["link"] as String

        with (mWebView!!) {
            loadUrl(url)

            with (settings) {

                cacheMode = when (NetworkUtils.isConnected(context!!)) {
                    true -> WebSettings.LOAD_DEFAULT
                    false -> WebSettings.LOAD_CACHE_ONLY
                }

                setAppCacheEnabled(true)
                allowContentAccess = true
                allowFileAccess = true
                databaseEnabled = true
                domStorageEnabled = true
                javaScriptEnabled = true
                builtInZoomControls = true
                displayZoomControls = false
            }

           webViewClient = NewsWebViewClient()
        }

        return mWebView
    }

    override fun onPause() {
        super.onPause()
        mWebView?.onPause()
    }

    override fun onResume() {
        mWebView?.onResume()
        super.onResume()
    }

    override fun onDestroyView() {
        mIsWebViewAvailable = false
        super.onDestroyView()
    }


    override fun onDestroy() {
        if (mWebView != null) {
            mWebView!!.destroy()
            mWebView = null
        }
        super.onDestroy()
    }

    fun getWebView(): WebView? {
        return if (mIsWebViewAvailable) mWebView else null
    }

}