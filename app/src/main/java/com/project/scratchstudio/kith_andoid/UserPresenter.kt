package com.project.scratchstudio.kith_andoid

import android.accounts.NetworkErrorException
import android.content.Context
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.network.model.user.UserListResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserPresenter(private val context: Context) {

    init {
        userApi = ApiClient.getClient(context).create(UserApi::class.java)
    }

    fun userParser(userBD: User, userModel: UserModelView): UserModelView {
        with(userModel) {
            id = userBD.id
            password = userBD.password
            url = userBD.url
            usersCount = userBD.usersCount
            firstName = userBD.firstName
            lastName = userBD.lastName
            middleName = userBD.middleName
            login = userBD.login
            phone = userBD.phone
            description = userBD.description
            position = userBD.position
            email = userBD.email
            photo = userBD.photo
            cityId = userBD.cityId
            countryId = userBD.countryId
            regionId = userBD.regionId
            referralCode = userBD.referralCode
        }
        return userModel
    }

    fun changePassword(callback: BaseCallback, userId: Int, newPassword: String) {
        disposable.add(
                userApi.changeUserPassword(userId, newPassword)
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

    fun getUser(callback: GetUserCallback, userId: Int) {
        disposable.add(
                userApi.getUser(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<User>() {
                            override fun onSuccess(response: User) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun editUser(callback: BaseCallback, newUser: UserModelView, newPhoto: PhotoModelView?) {
        disposable.add(
                userApi.editUser(newUser.id, newUser.firstName, newUser.lastName, newUser.middleName, newUser.phone, newUser.email, newUser.position,
                        newUser.description)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                            override fun onSuccess(response: BaseResponse) {
                                if (newPhoto != null) {
                                    editUserPhoto(object : BaseCallback {
                                        override fun onSuccess(baseResponse: BaseResponse) {
                                            callback.onSuccess(response)
                                        }

                                        override fun onError(networkError: NetworkErrorException) {
                                            callback.onError(NetworkErrorException(networkError))
                                        }
                                    }, newUser.id, newPhoto)
                                } else {
                                    callback.onSuccess(response)
                                }
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun editUserPhoto(callback: BaseCallback, userId: Int, newPhoto: PhotoModelView?) {
        val surveyBody = RequestBody.create(MediaType.parse("image/*"), newPhoto!!.phoneStorageFile)
        val result = MultipartBody.Part.createFormData("photo", newPhoto.phoneStorageFile.name, surveyBody)

        disposable.add(
                userApi.editUserPhoto(userId, result)
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
                        .subscribeWith(object : DisposableSingleObserver<UserListResponse>() {
                            override fun onSuccess(response: UserListResponse) {
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

    fun searchUsers(callback: GetUserListCallback, filterString: String) {
        disposable.add(
                userApi.searchUsers(filterString, "1", "50")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<UserListResponse>() {
                            override fun onSuccess(response: UserListResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    interface GetUserCallback {
        fun onSuccess(userResponse: User)

        fun onError(networkError: NetworkErrorException)
    }

    interface BaseCallback {
        fun onSuccess(baseResponse: BaseResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface GetUserListCallback {
        fun onSuccess(userResponse: UserListResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface ReferralCodeCallback {
        fun onSuccess(response: ReferralResponse)

        fun onError(networkError: NetworkErrorException)
    }

    companion object {

        internal lateinit var userApi: UserApi
        private val disposable = CompositeDisposable()
    }

}
