package com.project.scratchstudio.kith_andoid.SetInternalData;

import android.content.SharedPreferences;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;

public class SetUserIdAndToken implements ISetInternalData {

    @Override
    public int set(SharedPreferences sp) {
        SharedPreferences.Editor ed;
        ed = sp.edit();
        ed.putInt("user_id", HomeActivity.getMainUser().getId());
        ed.putString("user_token", HomeActivity.getMainUser().getToken());
        ed.apply();
        return 0;
    }

}
