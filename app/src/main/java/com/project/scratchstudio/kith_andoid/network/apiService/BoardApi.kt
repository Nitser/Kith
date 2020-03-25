package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.board.BoardsResponse
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BoardApi {

    @FormUrlEncoded
    @POST("api/boards/subscribe")
    fun subscribeAnnouncement(@Field("subscription_user_id") userId: String, @Field("subscription_board_id") boardId: String): Single<FavoriteResponse>

    @FormUrlEncoded
    @POST("api/boards/unsubscribe")
    fun unsubscribeAnnouncement(@Field("subscription_user_id") userId: String, @Field("subscription_board_id") boardId: String): Single<FavoriteResponse>

    @FormUrlEncoded
    @POST("api/boards/list")
    fun getBoards(@Field("user_id") userId: Int): Single<BoardsResponse>

    @GET("api/boards/getsubscribes/{id}")
    fun getFavoriteBoards(@Path("id") id: Int): Single<BoardsResponse>

    @GET("api/boards/getuserboards/{id}")
    fun getMyBoards(@Path("id") id: Int): Single<BoardsResponse>

    @FormUrlEncoded
    @POST("api/boards/create")
    fun createBoard(@Field("board_user_id") userId: Int, @Field("board_title") title: String,
                    @Field("board_description") description: String,
                    @Field("board_photo") photo: String, @Field("board_subscriptions") subscriptions: Int,
                    @Field("board_enabled") enabled: Int, @Field("board_cost") cost: String): Single<BaseResponse>

    @FormUrlEncoded
    @POST("api/boards/edit")
    fun editBoard(@Field("board_id") id: Int, @Field("board_user_id") userId: Int, @Field("board_title") title: String,
                  @Field("board_description") description: String, @Field("board_photo") photo: String,
                  @Field("board_subscriptions") subscriptions: Int,
                  @Field("board_enabled") enabled: Int, @Field("board_cost") cost: String): Single<BaseResponse>

}
