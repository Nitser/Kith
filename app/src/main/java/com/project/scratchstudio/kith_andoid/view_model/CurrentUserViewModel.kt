package com.project.scratchstudio.kith_andoid.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.scratchstudio.kith_andoid.model.UserModelView

class CurrentUserViewModel : ViewModel() {
    private val currentUser = MutableLiveData<UserModelView>()

    fun getCurrentUser(): LiveData<UserModelView> {
        return currentUser
    }

    fun setCurrentUser(user: UserModelView) {
        currentUser.value = user
    }

}