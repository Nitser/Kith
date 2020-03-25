package com.project.scratchstudio.kith_andoid.network.model

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    lateinit var error: String
        internal set

    @SerializedName("status")
    var status: Boolean = false
        internal set
}
