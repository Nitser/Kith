package com.project.scratchstudio.kith_andoid.network.model.entry

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class EntryResponse : BaseResponse() {
    @SerializedName("user_id")
    var userId: Int = 0

    @SerializedName("access_token")
    lateinit var token: String

    @SerializedName("token_type")
    lateinit var tokenType: String
}