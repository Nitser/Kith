package com.project.scratchstudio.kith_andoid.network.model.comment;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.user.User;

public class Comment {

    @SerializedName("message")
    private String message;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("user")
    private User user;

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }
}
