package com.project.scratchstudio.kith_andoid.network.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse extends BaseResponse {

    boolean status;

    @SerializedName("user")
    User user;

    @SerializedName("friend_count")
    int friendCount;

    @SerializedName("referral_count")
    int referalCount;

    public boolean getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }
}
