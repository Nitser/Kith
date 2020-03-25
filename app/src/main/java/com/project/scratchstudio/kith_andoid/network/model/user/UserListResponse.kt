package com.project.scratchstudio.kith_andoid.network.model.user

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

import java.util.ArrayList

class UserListResponse : BaseResponse() {

    @SerializedName("users")
    lateinit var users: ArrayList<User>

}
