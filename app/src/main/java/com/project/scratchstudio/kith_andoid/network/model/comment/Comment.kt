package com.project.scratchstudio.kith_andoid.network.model.comment

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.user.User

class Comment {

    @SerializedName("id")
    var id = 0

    @SerializedName("message")
    var message = ""

    @SerializedName("timestamp")
    var timestamp = ""

    @SerializedName("user")
    lateinit var user: User

    @SerializedName("board")
    var boardId = 0
}
