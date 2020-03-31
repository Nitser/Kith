package com.project.scratchstudio.kith_andoid

import android.accounts.NetworkErrorException
import android.content.Context
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class EntryPresenter(context: Context) {

    init {
        entryApi = ApiClient.getClient(context).create(EntryApi::class.java)
    }

    fun sendSMS(callback: BaseCallback, login: String) {
        disposable.add(
                entryApi.sendSMS(login)
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

    fun checkSMS(callback: EntryCallback, login: String, password: String, smsCode: String) {
        disposable.add(
                entryApi.checkSMS(login, password, smsCode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<EntryResponse>() {
                            override fun onSuccess(response: EntryResponse) {
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

    interface BaseCallback {
        fun onSuccess(baseResponse: BaseResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface EntryCallback {
        fun onSuccess(entryResponse: EntryResponse)

        fun onError(networkError: NetworkErrorException)
    }

    companion object {

        private lateinit var entryApi: EntryApi
        private val disposable = CompositeDisposable()
    }
}
