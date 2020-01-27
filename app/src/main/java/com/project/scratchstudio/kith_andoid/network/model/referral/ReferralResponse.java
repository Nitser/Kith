package com.project.scratchstudio.kith_andoid.network.model.referral;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;

public class ReferralResponse extends BaseResponse {
    @SerializedName("user_referral")
    String referral;

    public String getReferral() {
        return referral;
    }
}
