package com.project.scratchstudio.kith_andoid.network.model.referral

import com.google.gson.annotations.SerializedName
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse

class ReferralResponse : BaseResponse() {
    @SerializedName("user_referral")
    var referral: String = ""
        internal set

    @SerializedName("referral_count")
    var referralCount: Int = 0
        internal set
}
