package com.project.scratchstudio.kith_andoid.model

import android.graphics.Bitmap
import androidx.annotation.Keep
import java.io.File
import java.io.Serializable

@Keep
class PhotoModelView : Serializable {

    var photoInthernetPath = ""
    var photoPhoneStoragePath = ""
    lateinit var phoneStorageFile: File
    var photoBitmap: Bitmap? = null

}