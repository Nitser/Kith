package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.R;

public class AgreementActivity extends AppCompatActivity {

    private static long buttonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        WebView webView;
        webView = findViewById(R.id.help_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        if(isNetworkConnected())
            webView.loadUrl("http://kith.ga/terms");
        else Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
    }

    public void onClickBackButton(View view) {
        view.setEnabled(false);
        finish();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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
