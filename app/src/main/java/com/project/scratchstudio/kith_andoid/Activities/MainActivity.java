package com.project.scratchstudio.kith_andoid.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.Cache;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

//        CustomFontTextView counts = findViewById(R.id.customFontTextView2);
//        counts.setTypeface(null, Typeface.BOLD);

//        if(isNetworkConnected()){
//            HttpService httpService = new HttpService();
//            httpService.count(this, counts);
//
//        } else {
//            counts.setText(String.valueOf(Cache.getAllUsers()));
//            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
//        }


        Button button = findViewById(R.id.button);
        Button button1 = findViewById(R.id.button2);
        button.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/intro_regular.ttf"));
        button1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/intro_regular.ttf"));
    }

    public void signInButton(View view) {
        view.setEnabled(false);
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }


    public void registrationButton(View view) {
        view.setEnabled(false);
        Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
        startActivity(intent);
        finish();
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }
}
