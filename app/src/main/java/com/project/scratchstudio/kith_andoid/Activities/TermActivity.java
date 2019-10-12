package com.project.scratchstudio.kith_andoid.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.project.scratchstudio.kith_andoid.R;

public class TermActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_ativity);

        WebView webView;
        webView = findViewById(R.id.help_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://kith.ga/policy");

    }

    public void onClickBackButton(View view) {
        view.setEnabled(false);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

