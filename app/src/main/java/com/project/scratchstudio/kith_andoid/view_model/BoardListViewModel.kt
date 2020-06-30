package com.project.scratchstudio.kith_andoid.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.scratchstudio.kith_andoid.model.BoardModelView

class BoardListViewModel : ViewModel() {
    private val boardList = MutableLiveData<ArrayList<BoardModelView>>()
//    private val paidBoardList = MutableLiveData<ArrayList<BoardModelView>>()

    fun getBoardList(): LiveData<ArrayList<BoardModelView>> {
        return boardList
    }

    fun setBoardList(newBoardList: ArrayList<BoardModelView>) {
        boardList.postValue(newBoardList)
    }

//    fun getPaidBoardList(): LiveData<ArrayList<BoardModelView>> {
//        return paidBoardList
//    }
//
//    fun setPaidBoardList(newBoardList: ArrayList<BoardModelView>) {
//        paidBoardList.postValue(newBoardList)
//    }

}