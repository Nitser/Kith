package com.project.scratchstudio.kith_andoid

import android.accounts.NetworkErrorException
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.widget.CheckBox
import android.widget.Toast
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.board.Board
import com.project.scratchstudio.kith_andoid.network.model.category.CategoryResponse
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.Date

class BoardPresenter(private val context: Context) {

    init {
        boardApi = ApiClient.getClient(context).create(BoardApi::class.java)
    }

    fun dateParser(dateBD: String): String {
        val inputFormat: SimpleDateFormat
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

            val month_date = SimpleDateFormat("MMMM")

            val day_date = SimpleDateFormat("dd")
            val year_date = SimpleDateFormat("yyyy")

            var newCreationDateFormat: Date? = null
            if (dateBD != "null") {
                newCreationDateFormat = inputFormat.parse(dateBD.replace("\\s.*$".toRegex(), ""))
            }
            return "${day_date.format(newCreationDateFormat)} ${month_date.format(newCreationDateFormat)} ${year_date.format(newCreationDateFormat)}"
        } else {
            return ""
        }
    }

    fun boardParser(boardDB: Board): BoardModelView {
        val board = BoardModelView()
        with(board) {
            id = boardDB.id
            title = boardDB.title
            description = boardDB.description
            boardPhotoUrls = boardDB.boardPhotoUrls
            enabled = boardDB.enabled
            startDate = boardDB.startDate
            organizerId = boardDB.organizerId
            cost = boardDB.cost
            organizerFirstName = boardDB.organizerFirstName
            organizerLastName = boardDB.organizerLastName
            organizerMiddleName = boardDB.organizerMiddleName
            organizerPhone = boardDB.organizerPhone
            organizerPhotoUrl = boardDB.organizerPhotoUrl
            subscriptionOnBoard = boardDB.subscriptionOnBoard
            country = boardDB.country
            region = boardDB.region
            city = boardDB.city
            category = boardDB.category
            chatCount = boardDB.chatCount
        }
        return board
    }

    fun createBoard(callback: BoardCallback, board: BoardModelView) {

//        disposable.add(
//                boardApi.createBoard(board.title, board.description, enabled, board.cost, board.country?.id
//                        , board.region?.id, board.city?.id, board.category?.id)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
//                            override fun onSuccess(response: BaseResponse) {
//                                if (response.status) {
//                                    callback.onSuccess(response)
//                                }
//                            }
//
//                            override fun onError(e: Throwable) {
//                                callback.onError(NetworkErrorException(e))
//                            }
//                        })
//        )
    }

    val MULTIPART_FORM_DATA = "multipart/form-data"

    fun createRequestBody(stringField: String): RequestBody {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), stringField)
    }

//    fun createRequestBody(intField: Int): RequestBody {
//        return RequestBody.create(
//                MediaType.parse(MULTIPART_FORM_DATA), intField)
//    }

    fun editBoard(callback: BoardCallback, board: BoardModelView) {
//        val requestBody = HashMap<String, RequestBody>()
//        requestBody["board_title"] = createRequestBody(board.title)
//        requestBody["board_description"] = createRequestBody(board.description)
//        requestBody["board_id"] = createRequestBody(board.id.toString())
//        requestBody["board_enabled"] = createRequestBody(if (board.enabled) {
//            "1"
//        } else {
//            "0"
//        })
//        requestBody["board_price"] = createRequestBody(board.cost.toString())
//        if (board.country != null)
//            requestBody["country_id"] = createRequestBody(board.country?.id.toString())
//        if (board.region != null)
//            requestBody["region"] = createRequestBody(board.region?.id.toString())
//        if (board.city != null)
//            requestBody["city_id"] = createRequestBody(board.city?.id.toString())
//        if (board.category != null)
//            requestBody["category"] = createRequestBody(board.category?.id.toString())

        val photoParts = arrayOfNulls<MultipartBody.Part>(board.newPhotos.size)

        for (index in 0 until board.newPhotos.size) {
            val surveyBody = RequestBody.create(MediaType.parse("image/*"), board.newPhotos[index])
            photoParts[index] = MultipartBody.Part.createFormData("board_photo[]", board.newPhotos[index].name, surveyBody)
        }

        disposable.add(
                boardApi.editBoard(board.id, board.title, board.description, (if(board.enabled) {1} else {0})
                        , board.cost, board.country?.id, board.region?.id, board.city?.id, board.category?.id
                        , photoParts)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                            override fun onSuccess(response: BaseResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun getCategories(callback: CategoryCallback) {
        disposable.add(
                boardApi.getCategories("0", "50")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<CategoryResponse>() {
                            override fun onSuccess(response: CategoryResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun subscribeAnnouncement(board: BoardModelView, favorite: CheckBox) {
        HomeActivity.disposable.add(
                boardApi.subscribeAnnouncement(board.id.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                            override fun onSuccess(response: BaseResponse) {
                                board.subscriptionOnBoard = true
                                favorite.isChecked = true
                                Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                                favorite.isEnabled = true
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                favorite.isEnabled = true
                            }
                        })
        )
    }

    fun unsubscribeAnnouncement(board: BoardModelView, favorite: CheckBox) {
        HomeActivity.disposable.add(
                boardApi.unsubscribeAnnouncement(board.id.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                            override fun onSuccess(response: BaseResponse) {
                                board.subscriptionOnBoard = false
                                Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                                favorite.isChecked = false
                                favorite.isEnabled = true
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                favorite.isEnabled = true
                            }
                        })
        )
    }

    fun getComments(callback: CommentsCallback, boardId: Int, page: Int, size: Int) {
        disposable.add(
                boardApi.getComments(boardId, page.toString(), size.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Array<Comment>>() {
                            override fun onSuccess(response: Array<Comment>) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun sendComment(callback: BoardCallback, boardId: Int, message: String) {
        disposable.add(
                boardApi.sendComment(boardId, message)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                            override fun onSuccess(response: BaseResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    interface CategoryCallback {
        fun onSuccess(categoriesResponse: CategoryResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface CommentsCallback {
        fun onSuccess(commentsResponse: Array<Comment>)

        fun onError(networkError: NetworkErrorException)
    }

    interface BoardCallback {
        fun onSuccess(baseResponse: BaseResponse)

        fun onError(networkError: NetworkErrorException)
    }

    companion object {

        private lateinit var boardApi: BoardApi
        private val disposable = CompositeDisposable()
    }
}
