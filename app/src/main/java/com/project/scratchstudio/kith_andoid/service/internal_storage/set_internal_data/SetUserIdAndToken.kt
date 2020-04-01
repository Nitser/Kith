package com.project.scratchstudio.kith_andoid.service.internal_storage.set_internal_data

import android.content.SharedPreferences

import com.project.scratchstudio.kith_andoid.activities.HomeActivity

class SetUserIdAndToken : ISetInternalData {

    override fun set(sp: SharedPreferences) {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("cur_user_id", HomeActivity.mainUser.id)
        ed.putString("user_token", HomeActivity.mainUser.token)
        ed.putString("cur_user_password", HomeActivity.mainUser.password)
        ed.apply()
    }

}
