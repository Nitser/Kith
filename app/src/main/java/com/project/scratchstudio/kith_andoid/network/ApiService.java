package com.project.scratchstudio.kith_andoid.network;

import com.project.scratchstudio.kith_andoid.network.model.FavoriteResponse;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/boards/subscribe")
    Single<FavoriteResponse> subscribeAnnouncement(@Field("subscription_user_id") String userId, @Field("subscription_board_id") String boardId);

    @FormUrlEncoded
    @POST("api/boards/unsubscribe")
    Single<FavoriteResponse> unsubscribeAnnouncement(@Field("subscription_user_id") String userId, @Field("subscription_board_id") String boardId);

}
