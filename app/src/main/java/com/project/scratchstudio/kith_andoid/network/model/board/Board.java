package com.project.scratchstudio.kith_andoid.network.model.board;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;

public class Board extends BaseResponse {
    @SerializedName("board_id")
    public int id;

    @SerializedName("board_title")
    public String title;

    @SerializedName("board_enabled")
    public int enabled;

    @SerializedName("subscription_on_board")
    public int subscriptionOnBoard;

    @SerializedName("board_date_created")
    public String startDate;

    @SerializedName("board_date_end")
    public String endDate;

    @SerializedName("board_current_subscriptions")
    public String participants;

    @SerializedName("board_needs_subscriptions")
    public String needParticipants;

    @SerializedName("board_photo")
    public String url;

    @SerializedName("board_description")
    public String description;

    @SerializedName("board_user_id")
    public int organizerId;

    @SerializedName("user_phone")
    public String organizerPhone;

    @SerializedName("board_subscriptions")
    public int boardSubscription;

    @SerializedName("owner_firstname")
    public String organizerFirstName;

    @SerializedName("owner_lastname")
    public String organizerLastName;

    public String organizerName;

}
