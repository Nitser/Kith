package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface EntryApi {

    @GET("api/users/count")
    fun numberOfUsers(): Single<FavoriteResponse>

    @POST("api/singin")
    @FormUrlEncoded
    fun singIn(@Field("login") login: String, @Field("password") password: String): Single<EntryResponse>

    @POST("api/singup")
    @FormUrlEncoded
    fun singUp(@Field("user_firstname") firstName: String, @Field("user_lastname") lastName: String,
               @Field("user_middlename") middleName: String, @Field("user_phone") phone: String, @Field("user_login") login: String,
               @Field("user_password") password: String, @Field("invitation_user_id") invitationUserId: Int,
               @Field("user_position") position: String, @Field("user_description") description: String,
               @Field("photo") photo: String?): Single<EntryResponse>

    @POST("api/check_referral")
    @FormUrlEncoded
    fun checkReferralCode(@Field("user_referral") referralCode: String): Single<EntryResponse>

    @POST("api/users/confirm")
    @FormUrlEncoded
    fun sendSMS(@Field("user_login") login: String): Single<BaseResponse>

    @POST("api/users/checkcode")
    @FormUrlEncoded
    fun checkSMS(@Field("user_login") login: String, @Field("user_password") password: String,
                 @Field("sms_code") smsCode: String): Single<EntryResponse>
}