package com.project.scratchstudio.kith_andoid.network.model.user

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class UserResponse : BaseResponse() {
    @SerializedName("user")
    lateinit var user: User
        internal set

    @SerializedName("friend_count")
    internal var friendCount: Int = 0

    @SerializedName("referral_count")
    var referalCount: Int = 0
        internal set
}
