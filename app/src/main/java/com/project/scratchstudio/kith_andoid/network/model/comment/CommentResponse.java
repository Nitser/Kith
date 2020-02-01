package com.project.scratchstudio.kith_andoid.network.model.comment;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;

import java.util.ArrayList;

public class CommentResponse extends BaseResponse {

    @SerializedName("total")
    private int total;

    @SerializedName("comments")
    private ArrayList<Comment> comments;

    public int getTotal() {
        return total;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(final ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
