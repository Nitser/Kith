package com.project.scratchstudio.kith_andoid.network.model.category

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class Category : BaseResponse() {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name_en")
    lateinit var nameEn: String

    @SerializedName("name_native")
    var nameRu = ""

}
