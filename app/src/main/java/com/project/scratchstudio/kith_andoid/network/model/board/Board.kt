package com.project.scratchstudio.kith_andoid.network.model.board

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

import java.io.Serializable

class Board : BaseResponse(), Serializable {
    @SerializedName("board_id")
    var id: Int = 0

    @SerializedName("board_title")
    lateinit var title: String

    @SerializedName("board_enabled")
    var enabled: Int = 0

    @SerializedName("subscription_on_board")
    var subscriptionOnBoard: Int = 0

    @SerializedName("board_date_created")
    lateinit var startDate: String

    @SerializedName("board_date_end")
    lateinit var endDate: String

    @SerializedName("board_current_subscriptions")
    lateinit var participants: String

    @SerializedName("board_needs_subscriptions")
    lateinit var needParticipants: String

    @SerializedName("board_photo")
    lateinit var url: String

    @SerializedName("board_description")
    lateinit var description: String

    @SerializedName("board_user_id")
    var organizerId: Int = 0

    @SerializedName("user_phone")
    lateinit var organizerPhone: String

    @SerializedName("board_subscriptions")
    var boardSubscription: Int = 0

    @SerializedName("owner_firstname")
    lateinit var organizerFirstName: String

    @SerializedName("owner_lastname")
    lateinit var organizerLastName: String

    lateinit var organizerName: String

    @SerializedName("board_cost")
    lateinit var cost: String

}
