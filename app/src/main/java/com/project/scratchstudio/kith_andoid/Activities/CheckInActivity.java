package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.R;

public class CheckInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        CustomFontTextView t2 = findViewById(R.id.agree);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
        t2.setOnClickListener(view -> {
            Intent intent = new Intent(CheckInActivity.this, AgreementActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    public void onClickCheckInButton(View view) {
        Intent intent = new Intent(CheckInActivity.this, SignInActivity.class);
        startActivity(intent);
    }
}
