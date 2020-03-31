package com.project.scratchstudio.kith_andoid

import android.accounts.NetworkErrorException
import android.content.Context
import android.graphics.Bitmap
import com.project.scratchstudio.kith_andoid.service.PhotoService
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.board.Board
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.UnsupportedEncodingException

class BoardPresenter(private val context: Context) {

    init {
        boardApi = ApiClient.getClient(context).create(BoardApi::class.java)
    }

    fun createBoard(callback: BoardCallback, board: Board, photo: Bitmap?) {
        var res = ""
        if (photo != null) {
            val photoService = PhotoService(context)
            res = try {
                photoService.base64Photo(photo)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                ""
            }

        }
        disposable.add(
                boardApi.createBoard(board.organizerId, board.title, board.description, res, 0, 1, board.cost)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                            override fun onSuccess(response: BaseResponse) {
                                if (response.status) {
                                    callback.onSuccess(response)
                                }
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun editBoard(callback: BoardCallback, board: Board, photo: Bitmap?) {
        var res = ""
        if (photo != null) {
            val photoService = PhotoService(context)
            res = try {
                photoService.base64Photo(photo)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                ""
            }
        }
        disposable.add(
                boardApi.editBoard(board.organizerId, board.organizerId, board.title, board.description, res, 0, 1, board.cost)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                            override fun onSuccess(response: BaseResponse) {
                                if (response.status) {
                                    callback.onSuccess(response)
                                }
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
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
