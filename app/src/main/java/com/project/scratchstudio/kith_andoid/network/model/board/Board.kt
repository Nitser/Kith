package com.project.scratchstudio.kith_andoid.network.model.board

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.category.Category
import com.project.scratchstudio.kith_andoid.network.model.city.City
import com.project.scratchstudio.kith_andoid.network.model.country.Country
import com.project.scratchstudio.kith_andoid.network.model.region.Region
import java.io.Serializable

class Board : BaseResponse(), Serializable {

    @SerializedName("board_id")
    var id: Int = 0

    @SerializedName("board_title")
    var title = ""

    @SerializedName("board_description")
    var description = ""

    @SerializedName("board_photos")
    var boardPhotoUrls: ArrayList<Image> = ArrayList()

    @SerializedName("board_enabled")
    var enabled: Boolean = false

    @SerializedName("board_date_create")
    var startDate = ""

//    "board_date_end": null,

    @SerializedName("board_user_id")
    var organizerId: Int = 0

    @SerializedName("board_price")
    var cost: Int = 0

    @SerializedName("owner_firstname")
    var organizerFirstName = ""

    @SerializedName("owner_lastname")
    var organizerLastName = ""

    @SerializedName("owner_middlename")
    var organizerMiddleName = ""

    @SerializedName("owner_phone")
    var organizerPhone = ""

    @SerializedName("owner_photo")
    var organizerPhotoUrl: String? = ""

//    "owner_login": "besaint",

    @SerializedName("subscription_on_board")
    var subscriptionOnBoard: Boolean = false

    @SerializedName("country")
    var country: Country? = null

    @SerializedName("region")
    var region: Region? = null

    @SerializedName("city")
    var city: City? = null

    @SerializedName("category")
    var category: Category? = null

    @SerializedName("comments")
    var chatCount: Int = 0

    @SerializedName("board_highlight")
    var isPaid: Boolean = false


//    @SerializedName("board_date_end")
//    lateinit var endDate: String
//
//    @SerializedName("board_current_subscriptions")
//    lateinit var participants: String
//
//    @SerializedName("board_needs_subscriptions")
//    lateinit var needParticipants: String

//
//    @SerializedName("board_subscriptions")
//    var boardSubscription: Int = 0

}
