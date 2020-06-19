package com.project.scratchstudio.kith_andoid.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.model.PhotoModelView

class CurrentBoardViewModel : ViewModel() {
    private val currentBoard = MutableLiveData<BoardModelView>()
    private val newPhoto = MutableLiveData<ArrayList<PhotoModelView>>()

    fun getCurrentBoard(): LiveData<BoardModelView> {
        return currentBoard
    }

    fun getNewPhoto(): LiveData<ArrayList<PhotoModelView>> {
        return newPhoto
    }

    fun setNewPhoto(photo: ArrayList<PhotoModelView>) {
        newPhoto.postValue(photo)
    }

    fun setCurrentBoard(board: BoardModelView) {
        currentBoard.value = board
    }

}