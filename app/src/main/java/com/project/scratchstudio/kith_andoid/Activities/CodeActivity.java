package com.project.scratchstudio.kith_andoid.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;

public class CodeActivity extends AppCompatActivity {

    private ImageButton share;
    private static long buttonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        HttpService httpService = new HttpService();
        httpService.getReferral(this);

    }

    public void onClickBackButton(View view) {
        view.setEnabled(false);
        finish();
    }

    public void copyButton(View view) {
        TextView textView = findViewById(R.id.customFontTextView6);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("code", textView.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Код сохранен", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void shareButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        share = (ImageButton) view;
        TextView code = findViewById(R.id.customFontTextView6);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, code.getText().toString());
        sendIntent.setType("text/plain");
        startActivityForResult(sendIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 0 )
            share.setEnabled(true);
    }

}
