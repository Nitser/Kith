package com.project.scratchstudio.kith_andoid.network.model.board

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

import java.util.ArrayList

class BoardsResponse : BaseResponse() {

    @SerializedName("boards")
    lateinit var boards: ArrayList<Board>
}
