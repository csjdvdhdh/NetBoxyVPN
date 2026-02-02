package com.netboxy.vpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val VPN_REQUEST_CODE = 100
    private var pendingConfig: VlessConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWebView()
    }

    private fun setupWebView() {
        webView = findViewById(R.id.webView)
        
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = true
            allowContentAccess = true
        }

        // Add JavaScript Interface
        webView.addJavascriptInterface(WebAppInterface(), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Inject Android bridge functions
                webView.evaluateJavascript("""
                    window.androidConnect = function(config) {
                        Android.connect(config);
                    };
                    window.androidDisconnect = function() {
                        Android.disconnect();
                    };
                    window.androidGetStatus = function() {
                        return Android.getStatus();
                    };
                """.trimIndent(), null)
            }
        }

        // Load the HTML file from assets
        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class WebAppInterface {
        
        @JavascriptInterface
        fun connect(configJson: String) {
            runOnUiThread {
                try {
                    val config = Gson().fromJson(configJson, VlessConfig::class.java)
                    pendingConfig = config
                    
                    // Request VPN permission
                    val intent = VpnService.prepare(this@MainActivity)
                    if (intent != null) {
                        startActivityForResult(intent, VPN_REQUEST_CODE)
                    } else {
                        startVpnService(config)
                    }
                } catch (e: Exception) {
                    showToast("Error parsing configuration: ${e.message}")
                    notifyWebView("error", e.message ?: "Unknown error")
                }
            }
        }

        @JavascriptInterface
        fun disconnect() {
            runOnUiThread {
                stopVpnService()
            }
        }

        @JavascriptInterface
        fun getStatus(): String {
            val isConnected = com.netboxy.vpn.VpnService.isRunning
            return if (isConnected) "connected" else "disconnected"
        }

        @JavascriptInterface
        fun showToast(message: String) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == VPN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                pendingConfig?.let { config ->
                    startVpnService(config)
                }
            } else {
                showToast("VPN permission denied")
                notifyWebView("error", "VPN permission denied")
            }
            pendingConfig = null
        }
    }

    private fun startVpnService(config: VlessConfig) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val intent = Intent(this@MainActivity, com.netboxy.vpn.VpnService::class.java)
                intent.putExtra("config", Gson().toJson(config))
                startService(intent)
                
                // Wait a bit for service to start
                kotlinx.coroutines.delay(1000)
                
                if (com.netboxy.vpn.VpnService.isRunning) {
                    notifyWebView("connected", "Connected successfully")
                } else {
                    notifyWebView("error", "Failed to start VPN")
                }
            } catch (e: Exception) {
                notifyWebView("error", e.message ?: "Failed to start VPN")
            }
        }
    }

    private fun stopVpnService() {
        val intent = Intent(this, com.netboxy.vpn.VpnService::class.java)
        stopService(intent)
        notifyWebView("disconnected", "Disconnected")
    }

    private fun notifyWebView(status: String, message: String) {
        runOnUiThread {
            webView.evaluateJavascript(
                "if(window.onVpnStatusChange) window.onVpnStatusChange('$status', '$message');",
                null
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

data class VlessConfig(
    val uuid: String,
    val server: String,
    val port: String,
    val encryption: String,
    val security: String,
    val sni: String,
    val type: String,
    val host: String,
    val path: String,
    val name: String
)
