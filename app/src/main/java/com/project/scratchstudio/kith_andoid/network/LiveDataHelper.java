package com.project.scratchstudio.kith_andoid.network;

import com.project.scratchstudio.kith_andoid.network.model.comment.Comment;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class LiveDataHelper {
    private MediatorLiveData<List<Comment>> commentList = new MediatorLiveData<>();
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
        commentList.postValue(newCommentList);
    }

    public LiveData<List<Comment>> observeCommentList() {
        return commentList;
    }
}
