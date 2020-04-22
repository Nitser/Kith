package com.project.scratchstudio.kith_andoid.model

import android.graphics.Bitmap
import androidx.annotation.Keep
import java.io.Serializable

@Keep class UserModelView : Serializable {
    var id: Int = 0

    var token = ""
    var password = ""
    var url = ""
    var usersCount: Int = 0

    var firstName = ""

    var lastName = ""

    var middleName = ""

    var login = ""

    var phone = ""

    var description = ""

    var position = ""

    var email = ""

    var photo = ""

    //    @Transient
    var image: Bitmap? = null

    var city = ""
    var region = ""
    var country = ""
}