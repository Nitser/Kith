package com.project.scratchstudio.kith_andoid.network.model.user;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;

public class UserResponse extends BaseResponse {
    @SerializedName("user")
    User user;

    @SerializedName("friend_count")
    int friendCount;

    @SerializedName("referral_count")
    int referalCount;

    public User getUser() {
        return user;
    }

    public int getReferalCount() {
        return referalCount;
    }
}
