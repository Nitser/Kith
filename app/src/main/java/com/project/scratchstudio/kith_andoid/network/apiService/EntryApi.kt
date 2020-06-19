package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse
import com.project.scratchstudio.kith_andoid.network.model.city.CitiesResponse
import com.project.scratchstudio.kith_andoid.network.model.country.CountriesResponse
import com.project.scratchstudio.kith_andoid.network.model.region.RegionsResponse
import com.project.scratchstudio.kith_andoid.network.model.user.UserSingIn
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

@JvmSuppressWildcards
interface EntryApi {

    @POST("api/auth")
    @FormUrlEncoded
    fun singIn(@Field("login") login: String, @Field("password") password: String): Single<UserSingIn>

    @Multipart
    @POST("api/signup")
    fun singUp(@Part("user_firstname") firstName: String, @Part("user_lastname") lastName: String,
               @Part("user_phone") phone: String, @Part("user_login") login: String,
               @Part("user_password") password: String, @Part("invitation_user_id") invitationUserId: String,
               @Part("user_email") email: String,
               @Part("user_middlename") middleName: String?, @Part("user_position") position: String?,
               @Part("country_id") countryId: String?, @Part("region_id") regionId: String?,
               @Part("city_id") cityId: String?, @Part surveyImage: MultipartBody.Part?
    ): Single<NewBaseResponse>

    @POST("api/users/confirm")
    @FormUrlEncoded
    fun sendSMS(@Field("user_id") userId: Int): Single<NewBaseResponse>

    @POST("api/users/checkcode")
    @FormUrlEncoded
    fun checkSMS(@Field("user_id") userId: Int, @Field("sms_code") smsCode: String): Single<NewBaseResponse>

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