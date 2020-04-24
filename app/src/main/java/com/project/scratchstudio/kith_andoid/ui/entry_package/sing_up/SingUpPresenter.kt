package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.accounts.NetworkErrorException
import android.content.Context
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

class SingUpPresenter(context: Context) {

    init {
        entryApi = ApiClient.getClient(context).create(EntryApi::class.java)
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