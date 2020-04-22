package com.project.scratchstudio.kith_andoid.ui.entry_package.password_recovery

import android.accounts.NetworkErrorException
import android.content.Context
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class PasswordRecoveryPresenter(context: Context) {

    init {
        entryApi = ApiClient.getClient(context).create(EntryApi::class.java)
    }

    fun sendConfirmCode(callback: SendCodeCallback, emailOrLogin: String) {
        disposable.add(
                entryApi.sendConfirmCode(emailOrLogin)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<NewBaseResponse>() {
                            override fun onSuccess(response: NewBaseResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun checkConfirmCode(callback: SendCodeCallback, userId: Int, confirmCode: String) {
        disposable.add(
                entryApi.checkConfirmCode(userId, confirmCode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<NewBaseResponse>() {
                            override fun onSuccess(response: NewBaseResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun changeNewPassword(callback: SendCodeCallback, userId: Int, newPassword: String, doubleNewPassword: String) {
        disposable.add(
                entryApi.changeNewPassword(userId, newPassword, doubleNewPassword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<NewBaseResponse>() {
                            override fun onSuccess(response: NewBaseResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    interface SendCodeCallback {
        fun onSuccess(baseResponse: NewBaseResponse)

        fun onError(networkError: NetworkErrorException)
    }

    companion object {

        private lateinit var entryApi: EntryApi
        private val disposable = CompositeDisposable()
    }
}