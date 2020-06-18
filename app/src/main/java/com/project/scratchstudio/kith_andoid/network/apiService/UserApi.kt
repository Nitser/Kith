package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.network.model.user.UserListResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("api/users/{id}")
    fun getUser(@Path("id") id: Int): Single<User>

    @FormUrlEncoded
    @POST("api/users/referral")
    fun getReferralCode(@Field("user_id") id: Int): Single<ReferralResponse>

    @FormUrlEncoded
    @POST("api/users/referral_count")
    fun getReferralCount(@Field("user_id") id: Int, @Field("only_mine") isMine: Int?): Single<ReferralResponse>

    @GET("api/users")
    fun searchUsers(@Query("search") search: String, @Query("page") page: String,
                    @Query("size") size: String): Single<UserListResponse>

    @FormUrlEncoded
    @POST("api/users/referral_users")
    fun getInvitedUsers(@Field("user_id") id: Int, @Field("page") page: String,
                        @Field("size") size: String): Single<Array<User>>

    @FormUrlEncoded
    @POST("api/users/edit")
    fun editUser(@Field("user_id") id: Int, @Field("user_firstname") firstName: String, @Field("user_lastname") lastName: String,
                 @Field("user_middlename") middleName: String, @Field("user_phone") phone: String, @Field("user_email") email: String,
                 @Field("user_position") position: String, @Field("user_description") description: String,
                 @Field("user_photo") photo: String): Single<BaseResponse>

    @FormUrlEncoded
    @POST("api/users/edit/password")
    fun changePassword(@Field("user_id") id: Int, @Field("user_new_password") newPassword: String,
                       @Field("user_old_password") oldPassword: String): Single<BaseResponse>

}
