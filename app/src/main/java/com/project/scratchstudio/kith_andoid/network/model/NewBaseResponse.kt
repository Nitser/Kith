package com.project.scratchstudio.kith_andoid.network.model

import com.google.gson.annotations.SerializedName

open class NewBaseResponse {

    @SerializedName("user_id")
    var userId: Int = 0
        internal set

    @SerializedName("message")
    var message: String = ""
        internal set
}
