package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.comment.CommentResponse
import com.project.scratchstudio.kith_andoid.network.model.comment.SendCommentResponse

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApi {

    @FormUrlEncoded
    @POST("api/boards/{board_id}/comments")
    fun getComments(@Path("board_id") id: Int, @Field("page") page: String, @Field("size") size: String,
                    @Field("before") before: String): Single<CommentResponse>

    @FormUrlEncoded
    @POST("api/boards/{board_id}/comment")
    fun sendComment(@Path("board_id") id: Int, @Field("message") message: String): Single<SendCommentResponse>
}
