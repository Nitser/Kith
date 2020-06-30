package com.project.scratchstudio.kith_andoid.utils.internal_storage.set_internal_data

import android.content.SharedPreferences
import com.project.scratchstudio.kith_andoid.utils.internal_storage.InternalStorageService

class ClearUserIdAndToken : ISetInternalData {

    override fun set(sp: SharedPreferences, id: Int?, token: String?, password: String?) {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("cur_user_id", -1)
        ed.putString("user_token", "")
        ed.putString("cur_user_password", "")
        InternalStorageService.entryStatus = false
        ed.apply()
    }

}
