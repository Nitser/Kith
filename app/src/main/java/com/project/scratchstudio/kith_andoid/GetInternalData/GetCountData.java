package com.project.scratchstudio.kith_andoid.GetInternalData;

import android.content.SharedPreferences;

import com.project.scratchstudio.kith_andoid.Model.Cache;

public class GetCountData implements IGetInternalData {

    @Override
    public int get( SharedPreferences sp) {
        Cache.setAllUsers(sp.getInt("all_users", 0));
        return 0;
    }

}
