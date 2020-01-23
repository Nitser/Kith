package com.project.scratchstudio.kith_andoid.network;

import com.project.scratchstudio.kith_andoid.network.model.BoardsResponse;
import com.project.scratchstudio.kith_andoid.network.model.FavoriteResponse;
import com.project.scratchstudio.kith_andoid.network.model.UserResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/boards/subscribe")
    Single<FavoriteResponse> subscribeAnnouncement(@Field("subscription_user_id") String userId, @Field("subscription_board_id") String boardId);

    @FormUrlEncoded
    @POST("api/boards/unsubscribe")
    Single<FavoriteResponse> unsubscribeAnnouncement(@Field("subscription_user_id") String userId, @Field("subscription_board_id") String boardId);

    @FormUrlEncoded
    @POST("api/boards/list")
    Observable<BoardsResponse> getBoards(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("api/boards/getsubscribes/{id}")
    Observable<BoardsResponse> getFavoriteBoards(@Path("id") int id, @Field("user_id") int userId);

    @FormUrlEncoded
    @POST("api/boards/getuserboards/{id}")
    Observable<BoardsResponse> getMyBoards(@Path("id") int id, @Field("user_id") int userId);

    @GET("api/users/{id}")
    Single<UserResponse> getUser(@Path("id") int id);

}
