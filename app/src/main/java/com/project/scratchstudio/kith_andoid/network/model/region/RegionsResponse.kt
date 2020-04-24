package com.project.scratchstudio.kith_andoid.network.model.region

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class RegionsResponse : BaseResponse() {
    @SerializedName("regions")
    lateinit var regions: ArrayList<Region>
}