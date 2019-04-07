package com.project.scratchstudio.kith_andoid.SetInternalData;

import android.content.SharedPreferences;

public class ClearUserIdAndToken implements ISetInternalData {

    @Override
    public int set(SharedPreferences sp) {
        SharedPreferences.Editor ed;
        ed = sp.edit();
        ed.putInt("cur_user_id", -1);
        ed.putString("cur_user_token", "");
        ed.apply();
        String str = String.valueOf(sp.getInt("cur_user_id", -1));
        return 0;
    }

}
