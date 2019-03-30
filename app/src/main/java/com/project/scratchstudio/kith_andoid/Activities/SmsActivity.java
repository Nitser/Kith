package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;

public class SmsActivity extends AppCompatActivity {

    private HttpService httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        httpService = new HttpService();
        httpService.sendSms(this, HomeActivity.getMainUser().getId());
    }

    public void onClickBackButton(View view) {
        view.setEnabled(false);
        Intent intent = new Intent(SmsActivity.this, MainActivity.class);
        intent.putExtra("another_user", false);
        startActivity(intent);
        finish();
    }

    public void checkButton(View view) {
        view.setEnabled(false);
        CustomFontEditText smsCode = findViewById(R.id.editText7);
        httpService.checkSms(this, view, HomeActivity.getMainUser().getId(), String.valueOf(smsCode.getText()));
    }

    public void againButton(View view) {
        view.setEnabled(false);
        httpService = new HttpService();
        httpService.sendSms(this, HomeActivity.getMainUser().getId());

        CustomFontTextView text = (CustomFontTextView) view;
        text.setTextColor(getResources().getColor(R.color.colorHint));
        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                String s = "Отправить повторно через: " + millisUntilFinished/1000;
                text.setText(s);
            }

            public void onFinish() {
                text.setText("Отправить повторно");
                text.setTextColor(getResources().getColor(R.color.colorAccent));
                text.setEnabled(true);
            }
        }.start();

    }
}
