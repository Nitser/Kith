package com.project.scratchstudio.kith_andoid.network.model.region

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class Region : BaseResponse() {
//    "country": 1

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name_en")
    lateinit var nameEn: String

    @SerializedName("name_native")
    lateinit var nameRu: String

}
