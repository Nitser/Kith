package com.project.scratchstudio.kith_andoid.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.project.scratchstudio.kith_andoid.GetInternalData.GetCountData;
import com.project.scratchstudio.kith_andoid.GetInternalData.GetUserIdAndToken;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        InternalStorageService getCount = new InternalStorageService(this);
        getCount.setiGetInternalData(new GetCountData());
        getCount.execute();


        InternalStorageService getUser = new InternalStorageService(this);
        getUser.setiGetInternalData(new GetUserIdAndToken());
        getUser.execute();

    }
}
