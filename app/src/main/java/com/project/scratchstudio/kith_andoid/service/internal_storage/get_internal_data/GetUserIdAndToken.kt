package com.project.scratchstudio.kith_andoid.service.internal_storage.get_internal_data

import android.content.SharedPreferences
import com.project.scratchstudio.kith_andoid.activities.HomeActivity

class GetUserIdAndToken : IGetInternalData {

    override fun get(sp: SharedPreferences) {
        if (sp.getInt("cur_user_id", -1) != -1 && sp.getString("user_token", "") != "") {
            HomeActivity.createMainUser()
            HomeActivity.mainUser.id = sp.getInt("cur_user_id", -1)
            HomeActivity.mainUser.token = sp.getString("user_token", "") ?: ""
            HomeActivity.mainUser.password = sp.getString("cur_user_password", "") ?: ""
        }
    }

}
