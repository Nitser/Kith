package com.project.scratchstudio.kith_andoid.network.model;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    String error;

    @SerializedName("status")
    boolean status;

    public String getError() {
        return error;
    }

    public boolean getStatus() {
        return status;
    }
}
