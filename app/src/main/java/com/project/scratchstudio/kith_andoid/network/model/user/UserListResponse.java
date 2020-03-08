package com.project.scratchstudio.kith_andoid.network.model.user;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;

import java.util.ArrayList;

public class UserListResponse extends BaseResponse {

    @SerializedName("users")
    private ArrayList<User> users;

    public ArrayList<User> getUsers() {
        return users;
    }

}
