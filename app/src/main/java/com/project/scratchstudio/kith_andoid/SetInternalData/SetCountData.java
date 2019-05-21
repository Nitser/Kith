package com.project.scratchstudio.kith_andoid.SetInternalData;

import android.content.SharedPreferences;

import com.project.scratchstudio.kith_andoid.Model.Cache;

public class SetCountData implements ISetInternalData {
    @Override
    public int set(SharedPreferences sp) {
        SharedPreferences.Editor ed;
        ed = sp.edit();
        ed.putInt("count_users", Cache.getAllUsers());
        ed.apply();
        return 0;
    }
}
