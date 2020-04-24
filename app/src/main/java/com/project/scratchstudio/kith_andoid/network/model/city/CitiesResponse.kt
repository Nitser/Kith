package com.project.scratchstudio.kith_andoid.network.model.city

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class CitiesResponse : BaseResponse() {
    @SerializedName("cities")
    lateinit var cities: ArrayList<City>

}