package com.project.scratchstudio.kith_andoid.network.apiService;

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
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

    @FormUrlEncoded
    @POST("api/users/referral_users")
    Single<User[]> getInvitedUsers(@Field("user_id") int id, @Field("page") String page,
                                   @Field("size") String size);

    @FormUrlEncoded
    @POST("api/users/edit")
    Single<BaseResponse> editUser(@Field("user_id") int id, @Field("user_firstname") String firstName, @Field("user_lastname") String lastName,
                                  @Field("user_middlename") String middleName, @Field("user_phone") String phone, @Field("user_email") String email,
                                  @Field("user_position") String position, @Field("user_description") String description,
                                  @Field("user_photo") String photo);
}
