package com.project.scratchstudio.kith_andoid;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;

import com.project.scratchstudio.kith_andoid.Service.PhotoService;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;
import com.project.scratchstudio.kith_andoid.network.model.board.Board;

import java.io.UnsupportedEncodingException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class BoardPresenter {

    private static BoardApi boardApi;
    private static CompositeDisposable disposable = new CompositeDisposable();
    private Context context;

    public BoardPresenter(Context context) {
        this.context = context;
        boardApi = ApiClient.getClient(context).create(BoardApi.class);
    }

    public void createBoard(final BoardPresenter.CreateBoardCallback callback, Board board, Bitmap photo) {
        String res = "";
        if (photo != null) {
            PhotoService photoService = new PhotoService(context);
            try {
                res = photoService.base64Photo(photo);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                res = "";
            }
        }
        disposable.add(
                boardApi.createBoard(board.organizerId, board.title, board.description, res, 0, 1, board.cost)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                if (response.getStatus()) {
                                    callback.onSuccess(response);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                callback.onError(new NetworkErrorException(e));
                            }
                        })
        );
    }

    public interface CreateBoardCallback {
        void onSuccess(BaseResponse baseResponse);

        void onError(NetworkErrorException networkError);
    }
}
