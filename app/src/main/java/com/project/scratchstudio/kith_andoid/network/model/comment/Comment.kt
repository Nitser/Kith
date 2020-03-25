package com.project.scratchstudio.kith_andoid.network.model.comment

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.user.User

class Comment {

    @SerializedName("message")
    lateinit var message: String

    @SerializedName("timestamp")
    lateinit var timestamp: String

    @SerializedName("user")
    lateinit var user: User
}
