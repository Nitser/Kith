package com.project.scratchstudio.kith_andoid.service.internal_storage.get_internal_data

import android.content.SharedPreferences
import android.util.Log

import com.project.scratchstudio.kith_andoid.model.Cache

class GetCountData : IGetInternalData {

    override fun get(sp: SharedPreferences) {
        Cache.allUsers = sp.getInt("count_users", 0)
        Log.i("INTERNAL GET: ", "I'm here: " + Cache.allUsers)
    }

}
