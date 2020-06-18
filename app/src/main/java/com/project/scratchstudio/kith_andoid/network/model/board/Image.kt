package com.project.scratchstudio.kith_andoid.network.model.board

import com.google.gson.annotations.SerializedName

class Image {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("src")
    var src: String = ""
}