package com.project.scratchstudio.kith_andoid.activities

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.R

class AgreementActivity : AppCompatActivity() {

    private val isNetworkConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)
        val webView: WebView = findViewById(R.id.help_webview)
        webView.settings.javaScriptEnabled = true
        if (isNetworkConnected)
            webView.loadUrl("http://kith.ga/terms")
        else
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
    }

    fun onClickBackButton(view: View) {
        view.isEnabled = false
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {

        private val buttonCount: Long = 0
    }
}
