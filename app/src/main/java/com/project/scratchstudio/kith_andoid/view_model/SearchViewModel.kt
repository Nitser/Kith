package com.project.scratchstudio.kith_andoid.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.scratchstudio.kith_andoid.model.SearchOptions

class SearchViewModel : ViewModel() {
    private val searchOptions = MutableLiveData<SearchOptions>()

    fun getSearchOptions(): LiveData<SearchOptions> {
        return searchOptions
    }

    fun setSearchOptions(newSearchOptions: SearchOptions) {
        searchOptions.postValue(newSearchOptions)
    }

}