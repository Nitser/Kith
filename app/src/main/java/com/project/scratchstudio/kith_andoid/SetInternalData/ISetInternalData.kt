package com.project.scratchstudio.kith_andoid.SetInternalData

import android.content.SharedPreferences

interface ISetInternalData {

    fun set(sp: SharedPreferences): Int

}
