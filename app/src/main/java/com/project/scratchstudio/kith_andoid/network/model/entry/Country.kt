package com.project.scratchstudio.kith_andoid.network.model.entry

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class Country : BaseResponse() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name_en")
    lateinit var nameEn: String

    @SerializedName("name_native")
    lateinit var nameRu: String

    @SerializedName("phone_code")
    lateinit var phoneCode: String

}
