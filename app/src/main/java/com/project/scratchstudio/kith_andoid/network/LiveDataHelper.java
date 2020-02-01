package com.project.scratchstudio.kith_andoid.network;

import com.project.scratchstudio.kith_andoid.network.model.board.Board;
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class LiveDataHelper {
    private MediatorLiveData<List<Comment>> commentList = new MediatorLiveData<>();
    private MediatorLiveData<List<Board>> boardList = new MediatorLiveData<>();
    private static LiveDataHelper liveDataHelper;

    public LiveDataHelper() {
    }

    synchronized public static LiveDataHelper getInstance() {
        if (liveDataHelper == null) {
            liveDataHelper = new LiveDataHelper();
        }
        return liveDataHelper;
    }

    public void updateCommentList(List<Comment> newCommentList) {
        List<Comment> list = new ArrayList<>();
        if (commentList.getValue() != null) {
            list.addAll(commentList.getValue());
        }
        list.addAll(newCommentList);
        commentList.postValue(list);
    }

    public LiveData<List<Comment>> observeCommentList() {
        return commentList;
    }

    public void clearCommentList() {
        if (commentList.getValue() != null) {
            commentList.getValue().clear();
        }
    }

    public void updateBoardList(List<Board> newBoardList) {
        List<Board> list = new ArrayList<>();
        if (boardList.getValue() != null) {
            list.addAll(boardList.getValue());
        }
        list.addAll(newBoardList);
        boardList.postValue(list);
    }

    public LiveData<List<Board>> observeBoardList() {
        return boardList;
    }
}
