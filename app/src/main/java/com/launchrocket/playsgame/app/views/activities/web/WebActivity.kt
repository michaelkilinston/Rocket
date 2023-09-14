@file:Suppress("SameParameterValue")

package com.launchrocket.playsgame.app.views.activities.web

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.launchrocket.playsgame.app.R
import com.launchrocket.playsgame.app.helpful.PhotoFileManager
import com.launchrocket.playsgame.app.models.LinkContainer
import java.io.IOException

class WebActivity() : AppCompatActivity() {
    private lateinit var linkContainer: LinkContainer
    private lateinit var _we: WebView
    var we: WebView
        get() = _we
        set(value) {
            _we = value
        }
    var impFileCallback: ValueCallback<Array<Uri>>? = null
    private var someCallback: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        linkContainer = LinkContainer("").apply {
            intent?.run {
                link = this.getStringExtra("link").toString()
            }
        }
        onBackPressedDispatcher.addCallback( object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(we.canGoBack()) {
                    we.goBack()
                }
            }
        })
        we = findViewById(R.id.we)
        configureWebView()
    }

    private fun configureWebView() {
        startFromSettings()
        nextConfigureCookies()
        nowConfigureWebChromeClient()
        andConfigureWebViewClient()
        forEndLoadUrl()
    }

    private fun startFromSettings() {
        configureBooleanSettings()
        configureIntegerSettings()
        configureStringSettings()
    }

    private fun configureBooleanSettings() {
        val oneValue = true
        we.settings.allowContentAccess = oneValue
        we.settings.allowFileAccess = oneValue
        we.settings.javaScriptCanOpenWindowsAutomatically = oneValue
        we.settings.domStorageEnabled = oneValue
        we.settings.databaseEnabled = oneValue
        we.settings.useWideViewPort = oneValue
        we.settings.loadWithOverviewMode = oneValue
        configureBadBooleanSettings(oneValue)
    }

    @Suppress("DEPRECATION")
    private fun configureBadBooleanSettings(oneValue: Boolean) {
        we.settings.javaScriptEnabled = oneValue
        we.settings.allowUniversalAccessFromFileURLs = oneValue
        we.settings.allowFileAccessFromFileURLs = oneValue
    }

    private fun configureIntegerSettings() {
        we.settings.mixedContentMode = 0
        we.settings.cacheMode = WebSettings.LOAD_DEFAULT
    }

    private fun configureStringSettings() {
        val usrAgent = we.settings.userAgentString
        we.settings.userAgentString = usrAgent.replace("; wv", "")
    }

    private fun nextConfigureCookies() {
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(we, true)
    }

    private fun nowConfigureWebChromeClient() {
        we.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                impFileCallback = filePathCallback
                return true
            }
        }
    }

    private fun andConfigureWebViewClient() {
        we.webViewClient = Client()
    }

    private fun forEndLoadUrl() {
        we.loadUrl(linkContainer.link)
    }

    inner class Client : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val uri = request.url.toString()
            val condition = uri.contains("http") && uri.contains("/")
            if(!condition) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
            }
            return !condition
        }

        override fun onReceivedLoginRequest(
            view: WebView,
            realm: String,
            account: String?,
            args: String
        ) {
            super.onReceivedLoginRequest(view, realm, account, args)
        }
    }

    @Suppress("DEPRECATION")
    val requestPermissionLauncher = registerForActivityResult (
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean? ->

        val intents = arrayOf(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        val requestCode = 1
        val photoFileUri: Uri? =  try {
            PhotoFileManager.getTempFile("photo", ".jpg", this).toUri()
        } catch (except: IOException) {
            Log.e("Photo", "Unable to create photo. Something wrong.", except)
            null
        }
        intents.first().putExtra(
            MediaStore.EXTRA_OUTPUT,
            photoFileUri
        )
        someCallback = photoFileUri ?: Uri.EMPTY
        val current = Intent(OLD_INTENT_CATEGORY)
        current.type = OLD_INTENT_TYPE
        current.addCategory(OLD_INTENT_NEW_CATEGORY)
        fun putInChooserIntentExtras(intent: Intent, extraText: String, intents: Array<Intent>) {
            intent.putExtra(extraText, intents)
        }
        fun putInChooserIntentExtra(intent: Intent, extraText: String, intentForPut: Intent) {
            intent.putExtra(extraText, intentForPut)
        }
        val intentForChoose = Intent(Intent.ACTION_CHOOSER)
        putInChooserIntentExtras(intentForChoose, Intent.EXTRA_INITIAL_INTENTS, intents)
        putInChooserIntentExtra(intentForChoose, Intent.EXTRA_INTENT, current)
        startActivityForResult(intentForChoose, requestCode)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileCallback = impFileCallback
        if (fileCallback != null) {
            if (resultCode == -1) {
                val dataString = data?.dataString
                if (dataString != null) {
                    val dataStringUri = Uri.parse(dataString)
                    fileCallback.onReceiveValue(arrayOf(dataStringUri))
                } else {
                    fileCallback.onReceiveValue(if (someCallback != Uri.EMPTY) arrayOf(someCallback) else null)
                }
            } else {
                fileCallback.onReceiveValue(null)
            }
            impFileCallback = null
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        we.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        we.restoreState(savedInstanceState)
    }

    companion object {
        const val OLD_INTENT_CATEGORY = Intent.ACTION_GET_CONTENT
        const val OLD_INTENT_NEW_CATEGORY = Intent.CATEGORY_OPENABLE
        const val OLD_INTENT_TYPE = "*/*"
    }
}