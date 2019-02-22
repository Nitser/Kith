package com.project.scratchstudio.kith_andoid.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.R;

public class CodeActivity extends AppCompatActivity {

    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

    }

    public void onClickBackButton(View view) {
        Intent intent = new Intent(CodeActivity.this, HomeActivity.class);
        startActivity(intent);
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
            Intent intent = new Intent(CodeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void shareButton(View view) {
        TextView code = findViewById(R.id.customFontTextView6);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, code.getText().toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}