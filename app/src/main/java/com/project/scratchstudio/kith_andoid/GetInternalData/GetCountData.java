package com.project.scratchstudio.kith_andoid.GetInternalData;

import android.content.SharedPreferences;
import android.util.Log;

import com.project.scratchstudio.kith_andoid.Model.Cache;

public class GetCountData implements IGetInternalData {

    @Override
    public int get( SharedPreferences sp) {
        Cache.setAllUsers(sp.getInt("count_users", 0));
        Log.i("INTERNAL GET: ", "I'm here: " + Cache.getAllUsers());
        return 0;
    }

}
