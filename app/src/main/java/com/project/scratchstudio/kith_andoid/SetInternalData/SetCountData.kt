package com.project.scratchstudio.kith_andoid.SetInternalData

import android.content.SharedPreferences

import com.project.scratchstudio.kith_andoid.model.Cache

class SetCountData : ISetInternalData {
    override fun set(sp: SharedPreferences): Int {
        val ed: SharedPreferences.Editor = sp.edit()
        ed.putInt("count_users", Cache.allUsers)
        ed.apply()
        return 0
    }
}
