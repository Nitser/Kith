package com.project.scratchstudio.kith_andoid.network.model.referral;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;

public class ReferralResponse extends BaseResponse {
    boolean status;

    @SerializedName("user_referral")
    String referral;

    public boolean getStatus() {
        return status;
    }

    public String getReferral() {
        return referral;
    }
}
