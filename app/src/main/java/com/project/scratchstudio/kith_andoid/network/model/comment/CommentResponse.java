package com.project.scratchstudio.kith_andoid.network.model.comment;

import com.google.gson.annotations.SerializedName;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;

public class CommentResponse extends BaseResponse {

    @SerializedName("total")
    private int total;

    @SerializedName("comments")
    private Comment[] comments;

    public int getTotal() {
        return total;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(final Comment[] comments) {
        this.comments = comments;
    }
}
