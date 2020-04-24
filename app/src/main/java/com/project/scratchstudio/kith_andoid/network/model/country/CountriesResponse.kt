package com.project.scratchstudio.kith_andoid.network.model.country

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.city.City

class CountriesResponse : BaseResponse() {
    @SerializedName("countries")
    lateinit var countries: ArrayList<City>

}