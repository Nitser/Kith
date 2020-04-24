package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse
import com.project.scratchstudio.kith_andoid.network.model.city.CitiesResponse
import com.project.scratchstudio.kith_andoid.network.model.country.CountriesResponse
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse
import com.project.scratchstudio.kith_andoid.network.model.region.RegionsResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

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

    @POST("api/users/confirm")
    @FormUrlEncoded
    fun sendSMS(@Field("user_login") login: String): Single<BaseResponse>

    @POST("api/users/checkcode")
    @FormUrlEncoded
    fun checkSMS(@Field("user_login") login: String, @Field("user_password") password: String,
                 @Field("sms_code") smsCode: String): Single<EntryResponse>

    @POST("api/signup/check")
    @FormUrlEncoded
    fun checkField(@Field("field") field: String, @Field("value") value: String): Single<NewBaseResponse>

    @POST("api/users/referral")
    @FormUrlEncoded
    fun checkReferralCode(@Field("user_referral") referralCode: String): Single<NewBaseResponse>

    @GET("api/countries")
    fun getCountries(@Query("search") search: String, @Query("offset") offset: Int, @Query("size") size: Int): Single<CountriesResponse>

    @GET("api/regions")
    fun getRegions(@Query("country") countryId: Int, @Query("search") search: String, @Query("offset") offset: Int, @Query("size") size: Int): Single<RegionsResponse>

    @GET("api/cities")
    fun getCities(@Query("region") regionId: Int, @Query("search") search: String, @Query("offset") offset: Int, @Query("size") size: Int): Single<CitiesResponse>

    @POST("api/password/recovery")
    @FormUrlEncoded
    fun sendConfirmCode(@Field("user") user: String): Single<NewBaseResponse>

    @POST("api/password/confirm")
    @FormUrlEncoded
    fun checkConfirmCode(@Field("user_id") userId: Int, @Field("email_code") confirmCode: String): Single<NewBaseResponse>

    @PUT("api/password/change")
    @FormUrlEncoded
    fun changeNewPassword(@Field("user_id") userId: Int, @Field("first_password") password: String,
                          @Field("second_password") doublePassword: String): Single<NewBaseResponse>
}