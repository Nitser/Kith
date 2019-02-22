package com.project.scratchstudio.kith_andoid.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.project.scratchstudio.kith_andoid.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        Button button1 = findViewById(R.id.button2);
        button.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/intro_regular.ttf"));
        button1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/intro_regular.ttf"));
    }

    public void signInButton(View view) {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
    }


    public void registrationButton(View view) {
        Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            moveTaskToBack(true);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
