package com.project.scratchstudio.kith_andoid.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.project.scratchstudio.kith_andoid.R;

public class AgreementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
    }

    public void onClickBackButton(View view) {
        finish();
    }
}
