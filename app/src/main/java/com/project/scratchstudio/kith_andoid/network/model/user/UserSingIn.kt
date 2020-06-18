package com.project.scratchstudio.kith_andoid.network.model.user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserSingIn : Serializable {

    @SerializedName("user_id")
    var id: Int = 0

    @SerializedName("token")
    var token: String = ""

}
