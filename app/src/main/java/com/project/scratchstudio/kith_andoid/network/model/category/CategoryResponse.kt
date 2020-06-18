package com.project.scratchstudio.kith_andoid.network.model.category

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class CategoryResponse : BaseResponse() {

    @SerializedName("categories")
    var categories: ArrayList<Category> = ArrayList()

    @SerializedName("total")
    var total: Int = 0

}
