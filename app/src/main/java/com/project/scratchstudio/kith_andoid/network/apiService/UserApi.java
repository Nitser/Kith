package com.project.scratchstudio.kith_andoid.network.apiService;

import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.UserListResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {

    @GET("api/users/{id}")
    Single<UserResponse> getUser(@Path("id") int id);

    @FormUrlEncoded
    @POST("api/users/referral")
    Single<ReferralResponse> getReferralCode(@Field("user_id") int id);

    @FormUrlEncoded
    @POST("api/users/search")
    Single<UserListResponse> searchUsers(@Field("user_id") int id, @Field("search") String search, @Field("page") String page,
                                         @Field("size") String size);
}
