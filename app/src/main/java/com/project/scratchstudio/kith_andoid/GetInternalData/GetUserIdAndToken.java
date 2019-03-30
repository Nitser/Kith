package com.project.scratchstudio.kith_andoid.GetInternalData;

import android.content.SharedPreferences;
import android.util.Log;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;

public class GetUserIdAndToken implements IGetInternalData {

    @Override
    public int get(SharedPreferences sp) {
        if( sp.getInt("user_id", -1) == -1 ){
            return 1;
        }
        else {
            HomeActivity.createMainUser();
            HomeActivity.getMainUser().setId(sp.getInt("user_id", -1));
            HomeActivity.getMainUser().setToken(sp.getString("user_token", ""));
            return 2;
        }
    }

}
