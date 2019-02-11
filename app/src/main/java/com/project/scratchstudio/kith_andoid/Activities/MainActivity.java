package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.project.scratchstudio.kith_andoid.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signInButton(View view) {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
    }


    public void registrationButton(View view) {

    }
}