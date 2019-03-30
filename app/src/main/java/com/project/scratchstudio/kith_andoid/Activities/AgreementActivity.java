package com.project.scratchstudio.kith_andoid.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.project.scratchstudio.kith_andoid.R;

public class AgreementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        WebView webView;
        webView = findViewById(R.id.help_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://kith.ml/terms");

    }

    public void onClickBackButton(View view) {
        view.setEnabled(false);
        finish();
    }
}
