package com.project.scratchstudio.kith_andoid.service.internal_storage.set_internal_data

import android.content.SharedPreferences

interface ISetInternalData {

    fun set(sp: SharedPreferences, id: Int?, token: String?, password: String?)

}
