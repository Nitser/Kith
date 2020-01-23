package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Adapters.AnnouncementAdapter;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.ApiService;
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementFragment extends Fragment {

    private String type = "all";
    private Bundle bundle;
    private RecyclerView container;
    private AnnouncementAdapter adapter;

    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    public String getType() {
        return type;
    }

    private static List<AnnouncementInfo> listAnn = new ArrayList<>();

    public static List<AnnouncementInfo> getListAnn() {
        return listAnn;
    }

    public static void setListAnn(List<AnnouncementInfo> list) {
        listAnn = list;
    }

    public AnnouncementFragment() {
    }

    public static AnnouncementFragment newInstance(Bundle bundle) {
        AnnouncementFragment fragment = new AnnouncementFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        return inflater.inflate(R.layout.fragment_announcement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setButtonsListener();

        if (bundle != null && bundle.containsKey("type")) {
            type = bundle.getString("type");
        }

        if (isNetworkConnected()) {
            HttpService httpService = new HttpService();
            switch (type) {
                case "sub":
                    httpService.getSubscribedAnnouncement(getActivity(), HomeActivity.getMainUser(), this, false);
                    break;
                case "all":
                    httpService.getAnnouncements(getActivity(), HomeActivity.getMainUser(), this);
                    break;
                case "my":
                    httpService.getMyAnnouncement(getActivity(), HomeActivity.getMainUser(), this);
                    break;
            }
            changeSelectedButton();
        } else {
            Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }

        container = getActivity().findViewById(R.id.listCards);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        container.setLayoutManager(llm);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
    }

    public void subscribeAnnouncement(int user_id, AnnouncementInfo board, CheckBox favorite) {
        disposable.add(
                apiService.subscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FavoriteResponse>() {
                            @Override
                            public void onSuccess(FavoriteResponse response) {
                                if (response.getStatus()) {
                                    board.subscriptionOnBoard = 1;
                                    Toast.makeText(getContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                    favorite.setChecked(!favorite.isChecked());
                                }
                                favorite.setEnabled(true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("AnnouncementFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }

    public void unsubscribeAnnouncement(int user_id, AnnouncementInfo board, CheckBox favorite) {
        disposable.add(
                apiService.unsubscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FavoriteResponse>() {
                            @Override
                            public void onSuccess(FavoriteResponse response) {
                                if (response.getStatus()) {
                                    board.subscriptionOnBoard = 0;
                                    Toast.makeText(getContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                    favorite.setChecked(!favorite.isChecked());
                                }
                                favorite.setEnabled(true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("AnnouncementFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }

    public void setAdapter() {
        adapter = new AnnouncementAdapter(getActivity(), listAnn, (item, id) -> {
            bundle.putSerializable("board_list_id", id);
            bundle.putString("type", type);
            ((HomeActivity) getContext()).loadFragment(AnnouncementInfoFragment.newInstance(bundle));
        }, this);
        container.setAdapter(adapter);
    }

    private void setButtonsListener() {
        ImageButton back = getActivity().findViewById(R.id.plus);
        back.setOnClickListener(this::onClickAdd);

        Button all = getActivity().findViewById(R.id.all);
        Button sub = getActivity().findViewById(R.id.sub);
        Button my = getActivity().findViewById(R.id.my);

        all.setOnClickListener(this::onClickAll);
        sub.setOnClickListener(this::onClickSub);
        my.setOnClickListener(this::onClickMy);
    }

    private void changeSelectedButton() {
        Button all = getActivity().findViewById(R.id.all);
        Button sub = getActivity().findViewById(R.id.sub);
        Button my = getActivity().findViewById(R.id.my);
        switch (type) {
            case "sub":
                sub.setSelected(true);
                all.setSelected(false);
                my.setSelected(false);
                break;
            case "all":
                sub.setSelected(false);
                all.setSelected(true);
                my.setSelected(false);
                break;
            case "my":
                sub.setSelected(false);
                all.setSelected(false);
                my.setSelected(true);
                break;
        }
    }

    public void onClickAll(View view) {
        if (isNetworkConnected()) {
            HttpService httpService = new HttpService();
            httpService.getAnnouncements(getActivity(), HomeActivity.getMainUser(), this);
        } else {
            Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }
        type = "all";
        changeSelectedButton();
    }

    public void onClickSub(View view) {
        if (isNetworkConnected()) {
            HttpService httpService = new HttpService();
            httpService.getSubscribedAnnouncement(getActivity(), HomeActivity.getMainUser(), this, false);
        } else {
            Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }
        type = "sub";
        changeSelectedButton();
    }

    public void onClickMy(View view) {
        if (isNetworkConnected()) {
            HttpService httpService = new HttpService();
            httpService.getMyAnnouncement(getActivity(), HomeActivity.getMainUser(), this);
        } else {
            Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }
        type = "my";
        changeSelectedButton();
    }

    public void onClickAdd(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        bundle.putString("type", type);
        homeActivity.loadFragment(NewAnnouncementFragment.newInstance(bundle));
    }

    public boolean onBackPressed() {
        if (HomeActivity.getStackBundles().size() == 1) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(TreeFragment.newInstance(bundle));
            homeActivity.setTreeNavigation();
        } else {
            Bundle bundle = HomeActivity.getStackBundles().get(HomeActivity.getStackBundles().size() - 1);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(TreeFragment.newInstance(bundle));
            homeActivity.setTreeNavigation();
        }
        return true;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null);
    }
}
