package com.project.scratchstudio.kith_andoid.service.internal_storage.set_internal_data

import android.content.SharedPreferences

import com.project.scratchstudio.kith_andoid.model.Cache

class SetCountData : ISetInternalData {
    override fun set(sp: SharedPreferences) {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("count_users", Cache.allUsers)
        ed.apply()
    }
}
