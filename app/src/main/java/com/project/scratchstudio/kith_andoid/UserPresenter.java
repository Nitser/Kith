package com.project.scratchstudio.kith_andoid;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Activities.ProfileActivity;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse;

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
        Intent intent = new Intent(context, ProfileActivity.class);

        if (user.id == HomeActivity.getMainUser().id) {
            bundle.putBoolean("another_user", true);
            intent.putExtra("another_user", false);
            intent.putExtra("user", HomeActivity.getMainUser());
        } else {
            intent.putExtra("user", user);
        }

        context.startActivity(intent);
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

    public interface GetUserListCallback {
        void onSuccess(User[] userResponse);

        void onError(NetworkErrorException networkError);
    }

}
