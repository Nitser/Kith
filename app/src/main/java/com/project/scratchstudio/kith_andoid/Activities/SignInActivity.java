package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.scratchstudio.kith_andoid.R;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button button = findViewById(R.id.button2);
        button.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/intro_regular.ttf"));
    }

    public void signInButton(View view) {
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
