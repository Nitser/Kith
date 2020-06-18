package com.project.scratchstudio.kith_andoid.model

import android.graphics.Bitmap
import java.io.File

class PhotoModelView {

    var photoInthernetPath = ""
    var photoPhoneStoragePath = ""
    lateinit var phoneStorageFile: File
    var photoBitmap: Bitmap? = null

}