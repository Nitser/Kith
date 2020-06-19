package com.project.scratchstudio.kith_andoid.network.apiService

import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.board.BoardsResponse
import com.project.scratchstudio.kith_andoid.network.model.category.CategoryResponse
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface BoardApi {

    @GET("api/categories")
    fun getCategories(@Query("offset") offset: String, @Query("size") size: String): Single<CategoryResponse>

    @GET("api/comments/{board_id}")
    fun getComments(@Path("board_id") id: Int, @Query("page") page: String, @Query("size") size: String): Single<Array<Comment>>

    @FormUrlEncoded
    @PUT("api/comments")
    fun sendComment(@Field("board_id") id: Int, @Field("message") message: String): Single<BaseResponse>

    @FormUrlEncoded
    @PUT("api/favorites")
    fun subscribeAnnouncement(@Field("subscription_board_id") boardId: String): Single<BaseResponse>

    @DELETE("api/favorites")
    fun unsubscribeAnnouncement(@Query("board_id") boardId: String): Single<BaseResponse>

    @DELETE("api/board")
    fun deleteBoard(@Query("board_id") boardId: Int): Single<BaseResponse>

    @FormUrlEncoded
    @PUT("api/boards/archive/add")
    fun archiveAddBoard(@Field("board_id") boardId: Int): Single<BaseResponse>

    @DELETE("api/boards/archive/delete")
    fun archiveDeleteBoard(@Query("board_id") boardId: Int): Single<BaseResponse>

    @GET("api/boards/enabled")
    fun getBoards(@Query("size") size: Int, @Query("page") page: Int): Single<BoardsResponse>

    @GET("api/boards/enabled")
    fun searchBoards(@Query("search") search: String, @Query("category_id") categoryId: String,
                     @Query("country_id") countryId: String, @Query("region_id") regionId: String,
                     @Query("city_id") cityId: String, @Query("size") size: Int,
                     @Query("page") page: Int): Single<BoardsResponse>

    @GET("api/boards/getsubscribes/{id}")
    fun getFavoriteBoards(@Path("id") id: Int): Single<BoardsResponse>

    @GET("api/boards/getuserboards/{id}")
    fun getMyBoards(@Path("id") id: Int): Single<BoardsResponse>

    @Multipart
    @PUT("api/board")
    fun createBoard(@Part("board_title") title: String,
                    @Part("board_description") description: String?, @Part("board_enabled") enabled: Int,
                    @Part("board_price") cost: Int?, @Part("country_id") countryId: Int?,
                    @Part("region") regionId: Int?, @Part("city_id") cityId: Int?,
                    @Part("category") category: Int?,
                    @Part surveyImage: Array<MultipartBody.Part?>?): Single<BaseResponse>
//    fun createBoard(@Field("board_title") title: String, @Field("board_description") description: String,
//                    @Field("board_enabled") enabled: Int, @Field("board_price") cost: Int,
//                    @Field("country") country: Int?, @Field("region") region: Int?, @Field("city") city: Int?,
//                    @Field("category") category: Int?): Single<BaseResponse>

    @Multipart
    @POST("api/board")
    fun editBoard(@Part("board_id") id: Int, @Part("board_title") title: String,
                  @Part("board_description") description: String?, @Part("board_enabled") enabled: Int,
                  @Part("board_price") cost: Int?, @Part("country_id") countryId: Int?,
                  @Part("region") regionId: Int?, @Part("city_id") cityId: Int?,
                  @Part("category") category: Int?, @Part surveyImage: Array<MultipartBody.Part?>?): Single<BaseResponse>

}
