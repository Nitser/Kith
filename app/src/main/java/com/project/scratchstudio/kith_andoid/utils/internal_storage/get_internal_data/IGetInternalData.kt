package com.project.scratchstudio.kith_andoid.utils.internal_storage.get_internal_data

import android.content.SharedPreferences

interface IGetInternalData {

    operator fun get(sp: SharedPreferences)

}
