package com.project.scratchstudio.kith_andoid.Service;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Activities.MainActivity;
import com.project.scratchstudio.kith_andoid.GetInternalData.IGetInternalData;
import com.project.scratchstudio.kith_andoid.Model.Cache;
import com.project.scratchstudio.kith_andoid.SetInternalData.ISetInternalData;

public class InternalStorageService extends AsyncTask<String, Void, Integer> {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private Activity context;
    private IGetInternalData iGetInternalData;
    private ISetInternalData iSetInternalData;

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        switch (i){
            case 1:
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    context.finish();
                }, SPLASH_DISPLAY_LENGTH);
                break;
            case 2:
                new Handler().postDelayed(() -> {
                    Intent intent2 = new Intent(context, HomeActivity.class);
                    intent2.putExtra("another_user", false);
                    context.startActivity(intent2);
                    context.finish();
                }, SPLASH_DISPLAY_LENGTH);
                break;
        }
    }

    public InternalStorageService(Activity context){ this.context = context; }

    @Override
    protected Integer doInBackground(String... strings) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sp.contains("count_users")) {
            inizializingStorage(sp);
        }
        if (iGetInternalData != null) {
            return iGetInternalData.get(sp);
        }
        else
            return iSetInternalData.set(sp);
    }

    public void setiGetInternalData(IGetInternalData iGetInternalData) {
        this.iGetInternalData = iGetInternalData;
    }

    public void setiSetInternalData(ISetInternalData iSetInternalData) {
        this.iSetInternalData = iSetInternalData;
    }

    private void inizializingStorage(SharedPreferences sp){
        SharedPreferences.Editor ed;
        ed = sp.edit();
        ed.putInt("count_users", 0);
        ed.putInt("cur_user_id", -1 );
        ed.putString("cur_user_token", "");
        ed.apply();

        Cache.setAllUsers(0);
    }

}
