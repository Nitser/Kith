package com.project.scratchstudio.kith_andoid.network.model.user

import android.graphics.Bitmap

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class User : Serializable {

    @SerializedName("user_id")
    var id: Int = 0

    lateinit var token: String
    lateinit var password: String
    lateinit var url: String
    var usersCount: Int = 0

    @SerializedName("user_firstname")
    var firstName: String = ""

    @SerializedName("user_lastname")
    lateinit var lastName: String

    @SerializedName("user_middlename")
    var middleName: String = ""

    @SerializedName("user_login")
    lateinit var login: String

    @SerializedName("user_phone")
    lateinit var phone: String

    @SerializedName("user_description")
    var description: String = ""

    @SerializedName("user_position")
    lateinit var position: String

    @SerializedName("user_email")
    var email: String = ""

    @SerializedName("photo")
    var photo: String = ""

    @Transient
    var image: Bitmap? = null
}
