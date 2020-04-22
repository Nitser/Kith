package com.project.scratchstudio.kith_andoid.network.model.entry

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

 class CountriesResponse : BaseResponse() {
    @SerializedName("countries")
    lateinit var countries: ArrayList<Country>

}