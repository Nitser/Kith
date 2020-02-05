package com.project.scratchstudio.kith_andoid.UI.Comments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.CustomViews.EndlessRecyclerViewScrollListener;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.UI.Comments.list.DialogAdapter;
import com.project.scratchstudio.kith_andoid.UI.NewComment.NewCommentFragment;
import com.project.scratchstudio.kith_andoid.app.BaseFragment;
import com.project.scratchstudio.kith_andoid.app.FragmentType;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.LiveDataHelper;
import com.project.scratchstudio.kith_andoid.network.apiService.CommentApi;
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment;
import com.project.scratchstudio.kith_andoid.network.model.comment.CommentResponse;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CommentListFragment extends BaseFragment {
    private Bundle bundle;
    private int boardId;
    private String boardTitle;
    private RecyclerView listView;
    private DialogAdapter adapter;

    private CommentApi commentApi;
    private CompositeDisposable disposable = new CompositeDisposable();
    private LiveDataHelper liveDataHelper;

    public CommentListFragment() {
    }

    public static CommentListFragment newInstance(Bundle bundle) {
        CommentListFragment fragment = new CommentListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        boardId = bundle.getInt("board_id");
        boardTitle = bundle.getString("board_title");
        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        liveDataHelper = LiveDataHelper.getInstance();
        LiveDataHelper.getInstance().observeCommentList().observe(this, commentList -> {
            if (adapter.getItemCount() == 0) {
                adapter.setDialogList(commentList);
            }
            adapter.setDialogList(commentList);
            adapter.notifyDataSetChanged();
        });

        commentApi = ApiClient.getClient(getContext()).create(CommentApi.class);
        getComments(0, 10);

        setButtonsListener();

        listView = getActivity().findViewById(R.id.listDialog);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getComments(page, totalItemsCount);
            }
        };
        listView.addOnScrollListener(scrollListener);
        setAdapter();

        CustomFontTextView title = getActivity().findViewById(R.id.title);
        title.setText(boardTitle);
    }

    private void getComments(int page, int size) {
        disposable.add(
                commentApi.getComments(boardId, String.valueOf(page), String.valueOf(size), "2030-06-22 22:22:22")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CommentResponse>() {
                            @Override
                            public void onSuccess(CommentResponse response) {
                                for (Comment comment : response.getComments()) {
                                    comment.getUser().photo = comment.getUser().photo.replaceAll("\\/", "/");
                                }
                                Collections.reverse(response.getComments());
                                liveDataHelper.updateCommentList(response.getComments());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("GetComment Resp", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void setAdapter() {
        adapter = new DialogAdapter(getActivity());
        listView.setAdapter(adapter);
    }

    private void setButtonsListener() {
        ImageButton back = getActivity().findViewById(R.id.back_comment);
        back.setOnClickListener(this::onClickBack);
        LinearLayout send = getActivity().findViewById(R.id.new_c);
        send.setOnClickListener(this::onClickSend);
    }

    public void onClickBack(View view) {
        ((HomeActivity) getActivity()).backFragment();
    }

    public void onClickSend(View view) {
        ((HomeActivity) getActivity()).addFragment(NewCommentFragment.newInstance(bundle), FragmentType.NEW_COMMENT.name());
    }

    @Override
    public void onDestroy() {
        liveDataHelper.clearCommentList();
        super.onDestroy();
    }
}
