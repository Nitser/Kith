package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private boolean isChange = false;
    private static final int PASSWORD_CHANGED = 0;
    private static final int PASSWORD_NOT_CHANGED = 1;
    private static long buttonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

    }

    public void onClickChangePassword(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        CustomFontEditText oldPassword = findViewById(R.id.old_password);
        if(Objects.requireNonNull(oldPassword.getText()).toString().equals(HomeActivity.getMainUser().getPassword())){
            CustomFontEditText newPassword = findViewById(R.id.new_password);
            CustomFontEditText repPassword = findViewById(R.id.rep_password);
            if(Objects.requireNonNull(newPassword.getText()).toString().equals(Objects.requireNonNull(repPassword.getText()).toString())){
                if(isNetworkConnected()){
                    HttpService httpService = new HttpService();
                    httpService.changePassword(this, HomeActivity.getMainUser(), newPassword.getText().toString());
                } else{
                    view.setEnabled(true);
                    Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                }
            } else {
                view.setEnabled(true);
                Toast.makeText(this, "Новый пароль введен неверно", Toast.LENGTH_SHORT).show();
            }
        } else {
            view.setEnabled(true);
            Toast.makeText(this, "Старый пароль введен неверно", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveNewPassword(String password){
        Button button = findViewById(R.id.change);
        button.setEnabled(true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed;
        ed = sp.edit();
        ed.putString("cur_user_password", password);
        ed.apply();

        HomeActivity.getMainUser().setPassword(password);
        isChange = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(ChangePasswordActivity.this, EditActivity.class);
            if(isChange)
                setResult(PASSWORD_CHANGED, intent);
            else
                setResult(PASSWORD_NOT_CHANGED, intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void onClickCancel(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return;
        buttonCount = SystemClock.elapsedRealtime();
        Intent intent = new Intent(ChangePasswordActivity.this, EditActivity.class);
        if(isChange)
            setResult(PASSWORD_CHANGED, intent);
        else
            setResult(PASSWORD_NOT_CHANGED, intent);
        finish();
    }

    public void onClickDone(View view){
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return;
        buttonCount = SystemClock.elapsedRealtime();
        Intent intent = new Intent(ChangePasswordActivity.this, EditActivity.class);
        if(isChange)
            setResult(PASSWORD_CHANGED, intent);
        else
            setResult(PASSWORD_NOT_CHANGED, intent);
        finish();
    }

}
