package com.project.scratchstudio.kith_andoid.network

import com.project.scratchstudio.kith_andoid.network.model.board.Board
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import com.project.scratchstudio.kith_andoid.network.model.user.User

import java.util.ArrayList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class LiveDataHelper {
    private val userList = MediatorLiveData<List<User>>()
    private val commentList = MediatorLiveData<List<Comment>>()
    private val boardList = MediatorLiveData<List<Board>>()

    fun updateUserList(newUserList: List<User>) {
        val list = ArrayList<User>()
        if (userList.value != null) {
            list.addAll(userList.value!!)
        }
        list.addAll(newUserList)
        userList.postValue(list)
    }

    fun observeUserList(): LiveData<List<User>> {
        return userList
    }

    fun updateCommentList(newCommentList: List<Comment>) {
        val list = ArrayList<Comment>()
        if (commentList.value != null) {
            list.addAll(commentList.value!!)
        }
        list.addAll(newCommentList)
        commentList.postValue(list)
    }

    fun observeCommentList(): LiveData<List<Comment>> {
        return commentList
    }

    fun clearCommentList() {
        if (commentList.value != null) {
            commentList.value!!.clear()
        }
    }

    fun updateBoardList(newBoardList: List<Board>) {
        val list = ArrayList<Board>()
        if (boardList.value != null) {
            list.addAll(boardList.value!!)
        }
        list.addAll(newBoardList)
        boardList.postValue(list)
    }

    fun observeBoardList(): LiveData<List<Board>> {
        return boardList
    }

    companion object {
        private var liveDataHelper: LiveDataHelper? = null

        val instance: LiveDataHelper
            @Synchronized get() {
                if (liveDataHelper == null) {
                    liveDataHelper = LiveDataHelper()
                }
                return liveDataHelper!!
            }
    }
}
