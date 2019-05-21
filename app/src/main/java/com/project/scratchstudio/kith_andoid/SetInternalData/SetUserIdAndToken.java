package com.project.scratchstudio.kith_andoid.SetInternalData;

import android.content.SharedPreferences;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;

public class SetUserIdAndToken implements ISetInternalData {

    @Override
    public int set(SharedPreferences sp) {
        SharedPreferences.Editor ed;
        ed = sp.edit();
        ed.putInt("cur_user_id", HomeActivity.getMainUser().getId());
        ed.putString("cur_user_token", HomeActivity.getMainUser().getToken());
        ed.putString("cur_user_password", HomeActivity.getMainUser().getPassword());
        ed.apply();
        return 0;
    }

}
