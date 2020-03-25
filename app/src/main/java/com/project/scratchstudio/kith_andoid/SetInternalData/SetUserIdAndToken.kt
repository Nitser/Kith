package com.project.scratchstudio.kith_andoid.SetInternalData

import android.content.SharedPreferences

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity

class SetUserIdAndToken : ISetInternalData {

    override fun set(sp: SharedPreferences): Int {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("cur_user_id", HomeActivity.mainUser!!.id)
        ed.putString("user_token", HomeActivity.mainUser!!.token)
        ed.putString("cur_user_password", HomeActivity.mainUser!!.password)
        ed.apply()
        return 0
    }

}
