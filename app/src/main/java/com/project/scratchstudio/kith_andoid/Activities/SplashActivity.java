package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.scratchstudio.kith_andoid.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        new Handler().postDelayed(() -> {
//            Intent intent = new Intent(SplashActivity.this, .class);
//            startActivity(intent);
//            finish();
//        }, 3*1000);
    }
}
