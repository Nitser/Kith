package com.project.scratchstudio.kith_andoid;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Service.PhotoService;
import com.project.scratchstudio.kith_andoid.UI.UserProfile.ProfileFragment;
import com.project.scratchstudio.kith_andoid.app.FragmentType;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse;

import java.io.UnsupportedEncodingException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UserPresenter {

    private static UserApi userApi;
    private static CompositeDisposable disposable = new CompositeDisposable();
    private Context context;

    public UserPresenter(Context context) {
        this.context = context;
        userApi = ApiClient.getClient(context).create(UserApi.class);
    }

    public void openProfile(User user) {
        Bundle bundle = new Bundle();

        if (user.id == HomeActivity.getMainUser().id) {
            bundle.putBoolean("another_user", false);
            bundle.putSerializable("user", HomeActivity.getMainUser());
        } else {
            bundle.putBoolean("another_user", true);
            bundle.putSerializable("user", user);
        }
        ((HomeActivity) context).addFragment(ProfileFragment.newInstance(bundle), FragmentType.PROFILE.name());
    }

    public void getUser(final GetUserCallback callback, int userId) {
        disposable.add(
                userApi.getUser(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                callback.onSuccess(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callback.onError(new NetworkErrorException(e));
                            }
                        })
        );
    }

    public void editUser(final EditUserCallback callback, User newUser, Bitmap newPhoto) {
        String res = "";
//        if(newPhoto != null){
            try {
                PhotoService photoService = new PhotoService(context);
                res = photoService.base64Photo(newPhoto);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//        }

        disposable.add(
                userApi.editUser(newUser.id, newUser.firstName, newUser.lastName, newUser.middleName, newUser.phone, newUser.email, newUser.position,
                        newUser.description, res)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                callback.onSuccess(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callback.onError(new NetworkErrorException(e));
                            }
                        })
        );
    }

    public void getInvitedUsers(final GetUserListCallback callback, int userId) {
        disposable.add(
                userApi.getInvitedUsers(userId, "0", "50")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User[]>() {
                            @Override
                            public void onSuccess(User[] response) {
                                callback.onSuccess(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callback.onError(new NetworkErrorException(e));
                            }
                        })
        );
    }

    public interface GetUserCallback {
        void onSuccess(UserResponse userResponse);

        void onError(NetworkErrorException networkError);
    }

    public interface EditUserCallback {
        void onSuccess(BaseResponse baseResponse);

        void onError(NetworkErrorException networkError);
    }

    public interface GetUserListCallback {
        void onSuccess(User[] userResponse);

        void onError(NetworkErrorException networkError);
    }

}
