package com.project.scratchstudio.kith_andoid.network.model.comment

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

import java.util.ArrayList

class CommentResponse : BaseResponse() {

    @SerializedName("total")
    val total: Int = 0

    @SerializedName("comments")
    lateinit var comments: ArrayList<Comment>
}
