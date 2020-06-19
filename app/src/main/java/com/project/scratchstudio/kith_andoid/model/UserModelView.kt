package com.project.scratchstudio.kith_andoid.model

import android.graphics.Bitmap
import androidx.annotation.Keep
import java.io.File
import java.io.Serializable

@Keep class UserModelView : Serializable {
    var id: Int = 0

    var token = ""
    var password = ""
    var url = ""
    var usersCount = 0

    var firstName = ""

    var lastName = ""

    var middleName = ""

    var login = ""

    var phone = ""

    var description = ""

    var position = ""

    var email = ""

    var photo: String? = ""
    var photoPath = ""
    var photoFile : File? = null

    var photoBitmap: Bitmap? = null

    var cityId: Int? = -1
    var regionId: Int? = -1
    var countryId: Int? = -1
    var invitedUserId = -1

    var referralCode = ""
}