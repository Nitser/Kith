package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.network.model.user.UserListResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
                        @Field("size") size: String): Single<UserListResponse>

    @FormUrlEncoded
    @PUT("api/users/{id}")
    fun editUser(@Path("id") userId: Int, @Field("firstname") firstName: String, @Field("lastname") lastName: String,
                 @Field("middlename") middleName: String, @Field("phone") phone: String, @Field("email") email: String,
                 @Field("position") position: String, @Field("description") description: String): Single<BaseResponse>

    @Multipart
    @PUT("api/users/{id}")
    fun editUserPhoto(@Path("id") userId: Int, @Part surveyImage: MultipartBody.Part?): Single<BaseResponse>

    @FormUrlEncoded
    @PUT("api/users/{id}")
    fun changeUserPassword(@Path("id") userId: Int, @Field("password") password: String): Single<BaseResponse>

}
