package com.project.scratchstudio.kith_andoid.network.model.user

import android.graphics.Bitmap

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class User : Serializable {

    @SerializedName("id")
    var id: Int = 0

    var token: String = ""
    var password: String = ""
    var url: String = ""
    var usersCount: Int = 0

    @SerializedName("firstname")
    var firstName: String = ""

    @SerializedName("lastname")
    var lastName: String = ""

    @SerializedName("middlename")
    var middleName: String = ""

    @SerializedName("login")
    var login: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("description")
    var description: String = ""

    @SerializedName("position")
    var position: String = ""

    @SerializedName("email")
    var email: String = ""

    @SerializedName("photo")
    var photo: String? = ""

    @SerializedName("city_id")
    var cityId: Int? = 0
    @SerializedName("region_id")
    var regionId: Int? = 0
    @SerializedName("country_id")
    var countryId: Int? = 0

    @SerializedName("referral")
    var referralCode = ""

    @Transient
    var image: Bitmap? = null

    var city: String = ""
    var region: String = ""
    var country: String = ""
}
