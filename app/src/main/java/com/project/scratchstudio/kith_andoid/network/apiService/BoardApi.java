package com.project.scratchstudio.kith_andoid.network.apiService;

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;
import com.project.scratchstudio.kith_andoid.network.model.board.BoardsResponse;
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BoardApi {

    @FormUrlEncoded
    @POST("api/boards/subscribe")
    Single<FavoriteResponse> subscribeAnnouncement(@Field("subscription_user_id") String userId, @Field("subscription_board_id") String boardId);

    @FormUrlEncoded
    @POST("api/boards/unsubscribe")
    Single<FavoriteResponse> unsubscribeAnnouncement(@Field("subscription_user_id") String userId, @Field("subscription_board_id") String boardId);

    @FormUrlEncoded
    @POST("api/boards/list")
    Single<BoardsResponse> getBoards(@Field("user_id") int userId);

    @GET("api/boards/getsubscribes/{id}")
    Single<BoardsResponse> getFavoriteBoards(@Path("id") int id);

    @GET("api/boards/getuserboards/{id}")
    Single<BoardsResponse> getMyBoards(@Path("id") int id);

    @FormUrlEncoded
    @POST("api/boards/create")
    Single<BaseResponse> createBoard(@Field("board_user_id") int userId, @Field("board_title") String title,
                                     @Field("board_description") String description,
                                     @Field("board_photo") String photo, @Field("board_subscriptions") int subscriptions,
                                     @Field("board_enabled") int enabled, @Field("board_cost") String cost);

}
