package com.project.scratchstudio.kith_andoid

import android.accounts.NetworkErrorException
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.Service.PhotoService
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse
import com.project.scratchstudio.kith_andoid.ui.UserProfile.ProfileFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.UnsupportedEncodingException

class UserPresenter(private val context: Context) {

    init {
        userApi = ApiClient.getClient(context).create(UserApi::class.java)
    }

    fun changePassword(callback: BaseCallback, userId: Int, oldPassword: String, newPassword: String) {
        disposable.add(
                userApi.changePassword(userId, newPassword, oldPassword)
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

    fun openProfile(user: User) {
        val bundle = Bundle()

        if (user.id == HomeActivity.mainUser.id) {
            bundle.putBoolean("another_user", false)
            bundle.putSerializable("user", HomeActivity.mainUser)
        } else {
            bundle.putBoolean("another_user", true)
            bundle.putSerializable("user", user)
        }
        (context as HomeActivity).addFragment(ProfileFragment.newInstance(bundle), FragmentType.PROFILE.name)
    }

    fun getUser(callback: GetUserCallback, userId: Int) {
        disposable.add(
                userApi.getUser(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<UserResponse>() {
                            override fun onSuccess(response: UserResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun editUser(callback: BaseCallback, newUser: User, newPhoto: Bitmap?) {
        var res = ""
        try {
            val photoService = PhotoService(context)
            res = photoService.base64Photo(newPhoto)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        disposable.add(
                userApi.editUser(newUser.id, newUser.firstName, newUser.lastName, newUser.middleName, newUser.phone, newUser.email, newUser.position,
                        newUser.description, res)
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

    fun getInvitedUsers(callback: GetUserListCallback, userId: Int) {
        disposable.add(
                userApi.getInvitedUsers(userId, "0", "50")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Array<User>>() {
                            override fun onSuccess(response: Array<User>) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun getReferralCount(callback: ReferralCodeCallback, userId: Int, isMine: Int) {
        disposable.add(
                userApi.getReferralCount(userId, isMine)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<ReferralResponse>() {
                            override fun onSuccess(response: ReferralResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    interface GetUserCallback {
        fun onSuccess(userResponse: UserResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface BaseCallback {
        fun onSuccess(baseResponse: BaseResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface GetUserListCallback {
        fun onSuccess(userResponse: Array<User>)

        fun onError(networkError: NetworkErrorException)
    }

    interface ReferralCodeCallback {
        fun onSuccess(response: ReferralResponse)

        fun onError(networkError: NetworkErrorException)
    }

    companion object {

        private lateinit var userApi: UserApi
        private val disposable = CompositeDisposable()
    }

}
