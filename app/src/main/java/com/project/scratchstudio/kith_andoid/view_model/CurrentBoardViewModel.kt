package com.project.scratchstudio.kith_andoid.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.scratchstudio.kith_andoid.model.BoardModelView

class CurrentBoardViewModel : ViewModel() {
    private val currentBoard = MutableLiveData<BoardModelView>()

    fun getCurrentBoard(): LiveData<BoardModelView> {
        return currentBoard
    }

    fun setCurrentBoard(board: BoardModelView) {
        currentBoard.value = board
    }

}