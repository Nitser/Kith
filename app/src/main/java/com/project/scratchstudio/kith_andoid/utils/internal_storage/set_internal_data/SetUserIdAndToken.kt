package com.project.scratchstudio.kith_andoid.utils.internal_storage.set_internal_data

import android.content.SharedPreferences

class SetUserIdAndToken : ISetInternalData {

    override fun set(sp: SharedPreferences, id: Int?, token: String?, password: String?) {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("cur_user_id", id!!)
        ed.putString("user_token", token)
        ed.putString("cur_user_password", password)
        ed.apply()
    }

}
