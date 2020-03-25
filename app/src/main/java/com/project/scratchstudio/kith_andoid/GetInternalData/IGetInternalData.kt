package com.project.scratchstudio.kith_andoid.GetInternalData

import android.content.SharedPreferences

interface IGetInternalData {

    operator fun get(sp: SharedPreferences)

}
