package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.accounts.NetworkErrorException
import android.content.Context
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse
import com.project.scratchstudio.kith_andoid.network.model.city.CitiesResponse
import com.project.scratchstudio.kith_andoid.network.model.country.CountriesResponse
import com.project.scratchstudio.kith_andoid.network.model.region.RegionsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SingUpPresenter(context: Context) {

    init {
        entryApi = ApiClient.getClient(context).create(EntryApi::class.java)
    }

    private fun parseUserToFileOptions(user: UserModelView): List<MultipartBody.Part> {
        val options = ArrayList<MultipartBody.Part>()
        if (user.photoFile != null && user.photoFile!!.exists()) {
//            val file = File("/storage/emulated/0/Download/Corrections 6.jpg")
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), user.photoFile!!)
            val body = MultipartBody.Part.createFormData("photo", user.photoFile!!.name, requestFile)
            options.add(body)
        }
        return options
    }

    private fun parseUserToStringOptions(user: UserModelView): Map<String, String> {
        val options = HashMap<String, String>()
        val textType = "text/plain"

//        val firstName = RequestBody.create(MediaType.parse(textType), user.firstName)
//        val lastName = RequestBody.create(MediaType.parse(textType), user.lastName)
//        val phone = RequestBody.create(MediaType.parse(textType), user.phone)
//        val login = RequestBody.create(MediaType.parse(textType), user.login)
//        val password = RequestBody.create(MediaType.parse(textType), user.password)
//        val invitedUserId = RequestBody.create(MediaType.parse(textType), user.invitedUserId.toString())
//        val email = RequestBody.create(MediaType.parse(textType), user.email)

        with(user) {
            options["user_firstname"] = firstName
            options["user_lastname"] = lastName
            options["user_phone"] = phone
            options["user_login"] = login
            options["user_password"] = password
            options["invitation_user_id"] = user.invitedUserId.toString()
            options["user_email"] = email
        }

        if (user.middleName.isNotEmpty()) {
            val middleName = RequestBody.create(MediaType.parse("multipart/form-data"), user.middleName)
            options["user_middlename"] = user.middleName
        }

        if (user.position.isNotEmpty()) {
            val position = RequestBody.create(MediaType.parse("multipart/form-data"), user.position)
            options["user_position"] = user.position
        }

        if (user.countryId != -1) {
            val countryId = RequestBody.create(MediaType.parse("multipart/form-data"), user.countryId.toString())
            options["country_id"] = user.countryId.toString()
            if (user.regionId != -1) {
                val regionId = RequestBody.create(MediaType.parse("multipart/form-data"), user.regionId.toString())
                options["region_id"] = user.regionId.toString()
                if (user.cityId != -1) {
                    val cityId = RequestBody.create(MediaType.parse("multipart/form-data"), user.cityId.toString())
                    options["city_id"] = user.cityId.toString()
                }
            }
        }
        return options
    }

    fun singUp(callback: GetUserCallback, user: UserModelView) {
//        , parseUserToFileOptions(user)
        disposable.add(
                entryApi.singUp(parseUserToStringOptions(user))
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

    fun checkField(callback: GetUserCallback, fieldName: String, value: String) {
        disposable.add(
                entryApi.checkField(fieldName, value)
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

    fun checkReferralCode(callback: GetUserCallback, refCode: String) {
        disposable.add(
                entryApi.checkReferralCode(refCode)
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

    fun getCountries(callback: GetCountriesCallback, search: String) {
        disposable.add(
                entryApi.getCountries(search, 0, 4)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<CountriesResponse>() {
                            override fun onSuccess(response: CountriesResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun getRegions(callback: GetRegionsCallback, countryId: Int, search: String) {
        disposable.add(
                entryApi.getRegions(countryId, search, 0, 4)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<RegionsResponse>() {
                            override fun onSuccess(response: RegionsResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun getCities(callback: GetCitiesCallback, cityId: Int, search: String) {
        disposable.add(
                entryApi.getCities(cityId, search, 0, 4)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<CitiesResponse>() {
                            override fun onSuccess(response: CitiesResponse) {
                                callback.onSuccess(response)
                            }

                            override fun onError(e: Throwable) {
                                callback.onError(NetworkErrorException(e))
                            }
                        })
        )
    }

    fun sendSMS(callback: BaseCallback, userId: Int) {
        disposable.add(
                entryApi.sendSMS(userId)
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

    fun checkSMS(callback: BaseCallback, userId: Int, smsCode: String) {
        disposable.add(
                entryApi.checkSMS(userId, smsCode)
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

    interface BaseCallback {
        fun onSuccess(baseResponse: NewBaseResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface GetUserCallback {
        fun onSuccess(baseResponse: NewBaseResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface GetCountriesCallback {
        fun onSuccess(baseResponse: CountriesResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface GetRegionsCallback {
        fun onSuccess(response: RegionsResponse)

        fun onError(networkError: NetworkErrorException)
    }

    interface GetCitiesCallback {
        fun onSuccess(response: CitiesResponse)

        fun onError(networkError: NetworkErrorException)
    }

    companion object {

        private lateinit var entryApi: EntryApi
        private val disposable = CompositeDisposable()
    }
}