package com.project.scratchstudio.kith_andoid.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView

import com.project.scratchstudio.kith_andoid.R

class TermActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_ativity)

        val webView: WebView = findViewById(R.id.help_webview)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("http://kith.ga/policy")

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
}

