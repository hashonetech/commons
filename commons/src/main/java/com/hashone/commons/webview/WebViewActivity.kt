package com.hashone.commons.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import com.hashone.commons.base.BaseActivity
import com.hashone.commons.databinding.ActivityWebviewBinding
import com.hashone.commons.extensions.isNetworkAvailable


class WebViewActivity : BaseActivity() {

    private lateinit var mBinding: ActivityWebviewBinding

    private var mCurrentUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mCurrentUrl = intent!!.extras!!.getString("url", "")

        setSupportActionBar(mBinding.toolBarWebView)

        mBinding.textViewWebViewTitle.text = intent!!.extras!!.getString("title", "")

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        mBinding.buttonRetryWebView.setOnClickListener {
            if (isNetworkAvailable()) {
                loadStringInWebView()
                mBinding.layoutEmptyWebView.isVisible = false
                mBinding.progressBarPrivacy.isVisible = false
            } else {
                mBinding.layoutEmptyWebView.isVisible = true
            }
        }

        mBinding.layoutEmptyWebView.isVisible = false
        mBinding.progressBarPrivacy.isVisible = false

        if (isNetworkAvailable()) {
            loadStringInWebView()
            mBinding.layoutEmptyWebView.isVisible = false
            mBinding.progressBarPrivacy.isVisible = false
        } else {
            mBinding.layoutEmptyWebView.isVisible = true
        }
    }

    override fun onDestroy() {
        if (::mBinding.isInitialized) {
            mBinding.webViewPrivacy.destroy()
        }
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadStringInWebView() {
        mBinding.webViewPrivacy.settings.javaScriptEnabled = true
        mBinding.webViewPrivacy.loadUrl(mCurrentUrl)

        mBinding.webViewPrivacy.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                mBinding.progressBarPrivacy.isVisible = newProgress in 6..84
            }
        }

        mBinding.webViewPrivacy.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (view != null && request != null) {
                    if (request.url != null) {
                        view.loadUrl(request.url.toString())
                    }
                }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (view != null && url != null)
                    view.loadUrl(url)
                return false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}