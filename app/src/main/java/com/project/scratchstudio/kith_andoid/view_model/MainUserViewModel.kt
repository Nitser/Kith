package com.project.scratchstudio.kith_andoid.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.scratchstudio.kith_andoid.model.UserModelView

class MainUserViewModel : ViewModel() {
    private val mainUser = MutableLiveData<UserModelView>()

    fun getMainUser(): LiveData<UserModelView> {
        return mainUser
    }

    fun setMainUser(board: UserModelView) {
        mainUser.value = board
    }

}