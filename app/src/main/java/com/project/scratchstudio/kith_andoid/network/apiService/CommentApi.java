package com.project.scratchstudio.kith_andoid.network.apiService;

import com.project.scratchstudio.kith_andoid.network.model.comment.CommentResponse;
import com.project.scratchstudio.kith_andoid.network.model.comment.SendCommentResponse;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentApi {

    @FormUrlEncoded
    @POST("api/boards/{board_id}/comments")
    Single<CommentResponse> getComments(@Path("board_id") int id, @Field("page") String page, @Field("size") String size,
                                        @Field("before") String before);

    @FormUrlEncoded
    @POST("api/boards/{board_id}/comment")
    Single<SendCommentResponse> sendComment(@Path("board_id") int id, @Field("message") String message);
}
