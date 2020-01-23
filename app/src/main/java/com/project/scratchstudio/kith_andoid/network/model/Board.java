package com.project.scratchstudio.kith_andoid.network.model;

import com.google.gson.annotations.SerializedName;

public class Board extends BaseResponse {
    @SerializedName("board_id")
    public int id;

    @SerializedName("board_title")
    public String title;

//    public int enabled;
//    public int boardSubscription;
//    public int subscriptionOnBoard;
//
//    public String startDate;
//    public String endDate;
//
//    public String participants;
//    public String needParticipants;
//    public String url;
//    public String description;
//
//    public int organizerId;
//    public String organizerName;
//    public String organizerLastName;
//    public String organizerMiddleName;
//    public String organizerLogin;
//    public String organizerPhoto;
//    public String organizerPhone;
}
