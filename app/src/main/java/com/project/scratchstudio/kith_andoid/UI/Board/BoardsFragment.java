package com.project.scratchstudio.kith_andoid.UI.Board;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Fragments.AnnouncementInfoFragment;
import com.project.scratchstudio.kith_andoid.Fragments.NewAnnouncementFragment;
import com.project.scratchstudio.kith_andoid.Fragments.TreeFragment;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.UI.Board.list.BoardAdapter;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi;
import com.project.scratchstudio.kith_andoid.network.model.board.Board;
import com.project.scratchstudio.kith_andoid.network.model.board.BoardsResponse;
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class BoardsFragment extends Fragment {

    private BoardType type = BoardType.ALL;
    private Bundle bundle;
    private RecyclerView container;
    private BoardAdapter adapter;

    private BoardApi boardApi;
    private CompositeDisposable disposable = new CompositeDisposable();

    private static List<Board> listAnn = new ArrayList<>();

    public static List<Board> getListAnn() {
        return listAnn;
    }

    public static void setListAnn(List<Board> list) {
        listAnn = list;
    }

    public BoardsFragment() {
    }

    public static BoardsFragment newInstance(Bundle bundle) {
        BoardsFragment fragment = new BoardsFragment();
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
        boardApi = ApiClient.getClient(getContext()).create(BoardApi.class);

        if (bundle != null && bundle.containsKey("type")) {
            type = (BoardType) bundle.getSerializable("type");
        }

        setButtonsListener();
        loadBoards(type);

        container = getActivity().findViewById(R.id.listCards);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        container.setLayoutManager(llm);
        setAdapter();
    }

    public void subscribeAnnouncement(int user_id, Board board, CheckBox favorite) {
        disposable.add(
                boardApi.subscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
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
                                Log.e("BoardsFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }

    public void unsubscribeAnnouncement(int user_id, Board board, CheckBox favorite) {
        disposable.add(
                boardApi.unsubscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
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
                                Log.e("BoardsFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }

    public void setAdapter() {
        adapter = new BoardAdapter(getActivity(), (item, id) -> {
            bundle.putSerializable("board_list_id", id);
            bundle.putSerializable("type", type);
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

        all.setOnClickListener(v -> loadBoards(BoardType.ALL));
        sub.setOnClickListener(v -> loadBoards(BoardType.SUB));
        my.setOnClickListener(v -> loadBoards(BoardType.MY));
    }

    private void changeSelectedButton() {
        Button all = getActivity().findViewById(R.id.all);
        Button sub = getActivity().findViewById(R.id.sub);
        Button my = getActivity().findViewById(R.id.my);
        switch (type) {
            case SUB:
                sub.setSelected(true);
                all.setSelected(false);
                my.setSelected(false);
                break;
            case ALL:
                sub.setSelected(false);
                all.setSelected(true);
                my.setSelected(false);
                break;
            case MY:
                sub.setSelected(false);
                all.setSelected(false);
                my.setSelected(true);
                break;
        }
    }

    private void loadBoards(BoardType newType) {
        Disposable dis;
        switch (newType) {
            case ALL: {
                dis = boardApi.getBoards(HomeActivity.getMainUser().id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BoardsResponse>() {
                            @Override
                            public void onSuccess(BoardsResponse response) {
                                changeBoardDate(newType, response.getBoards());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("GetAllBoard Resp", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            }
            case SUB: {
                dis = boardApi.getFavoriteBoards(HomeActivity.getMainUser().id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BoardsResponse>() {
                            @Override
                            public void onSuccess(BoardsResponse response) {
                                changeBoardDate(newType, response.getBoards());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("GetAllBoard Resp", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            }
            default: {
                dis = boardApi.getMyBoards(HomeActivity.getMainUser().id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BoardsResponse>() {
                            @Override
                            public void onSuccess(BoardsResponse response) {
                                changeBoardDate(newType, response.getBoards());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("GetAllBoard Resp", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        disposable.add(dis);
    }

    private void changeBoardDate(BoardType boardType, ArrayList<Board> boards) {
        for (Board board : boards) {
            board.organizerName = board.organizerFirstName + " " + board.organizerLastName;
            board.url = board.url.replaceAll("\\/", "/");
        }
        type = boardType;
        changeSelectedButton();
        adapter.setAnnList(boards);
        adapter.notifyDataSetChanged();
    }

    private void onClickAdd(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        bundle.putSerializable("type", type);
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
}
