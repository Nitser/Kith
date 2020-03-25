package com.project.scratchstudio.kith_andoid.SetInternalData

import android.content.SharedPreferences

class ClearUserIdAndToken : ISetInternalData {

    override fun set(sp: SharedPreferences): Int {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("cur_user_id", -1)
        ed.putString("user_token", "")
        ed.putString("cur_user_password", "")
        ed.apply()
        return 0
    }

}
