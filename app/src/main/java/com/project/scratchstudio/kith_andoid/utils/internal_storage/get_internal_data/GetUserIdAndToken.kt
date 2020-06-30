package com.project.scratchstudio.kith_andoid.utils.internal_storage.get_internal_data

import android.content.SharedPreferences
import com.project.scratchstudio.kith_andoid.utils.internal_storage.InternalStorageService

class GetUserIdAndToken : IGetInternalData {

    override fun get(sp: SharedPreferences) {
        if (sp.getInt("cur_user_id", -1) != -1 && sp.getString("user_token", "") != "") {
            InternalStorageService.user.id = sp.getInt("cur_user_id", -1)
            InternalStorageService.user.token = sp.getString("user_token", "") ?: ""
            InternalStorageService.user.password = sp.getString("cur_user_password", "") ?: ""
            InternalStorageService.entryStatus = true
        }
    }

}
