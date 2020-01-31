package com.project.scratchstudio.kith_andoid.Fragments;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Adapters.DialogAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.CommentApi;
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment;
import com.project.scratchstudio.kith_andoid.network.model.comment.CommentResponse;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DialogFragment extends Fragment {
    private static long buttonCount = 0;
    private Bundle bundle;
    private int boardId;
    private String boardTitle;
    private RecyclerView listView;
    private DialogAdapter adapter;
    private CommentApi commentApi;
    private CompositeDisposable disposable = new CompositeDisposable();

    private static List<Comment> listMessages;

    public DialogFragment() {
    }

    public static DialogFragment newInstance(Bundle bundle) {
        DialogFragment fragment = new DialogFragment();
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
        commentApi = ApiClient.getClient(getContext()).create(CommentApi.class);
        getComments();

        setButtonsListener();

        listView = getActivity().findViewById(R.id.listDialog);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);
        CustomFontTextView title = getActivity().findViewById(R.id.title);
        title.setText(boardTitle);
    }

    private void getComments() {
        disposable.add(
                commentApi.getComments(boardId, "0", "50", "2019-06-22 22:22:22")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CommentResponse>() {
                            @Override
                            public void onSuccess(CommentResponse response) {
                                for (Comment comment : response.getComments()) {
                                    comment.getUser().photo = comment.getUser().photo.replaceAll("\\/", "/");
                                }
                                listMessages = Arrays.asList(response.getComments());
                                setAdapter();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("BoardFragmentInfo", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    public void setAdapter() {
        adapter = new DialogAdapter(getActivity(), listMessages, item -> {
            if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
                return;
            }
            buttonCount = SystemClock.elapsedRealtime();
        });
//        listView.smoothScrollToPosition(adapter.getItemCount()-1);
        listView.setAdapter(adapter);
    }

    private void setButtonsListener() {
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        LinearLayout send = getActivity().findViewById(R.id.new_c);
        send.setOnClickListener(this::onClickSend);
    }

    public void onClickBack(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(AnnouncementInfoFragment.newInstance(bundle));
    }

    public boolean onBackPressed() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(AnnouncementInfoFragment.newInstance(bundle));
        return true;
    }

    public void onClickSend(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(NewCommentFragment.newInstance(bundle));
    }

    public void createNewComment(String message) {
//        DialogInfo newDialog = new DialogInfo();
//        newDialog.user_id = HomeActivity.getMainUser().getId();
//        newDialog.message = message;
//        newDialog.photo = HomeActivity.getMainUser().getUrl();
//
//        adapter.newItem = true;
//        listMessages.add(newDialog);
//        listView.smoothScrollToPosition(adapter.getItemCount() - 1);
//        adapter.notifyDataSetChanged();
//        listView.scrollToPosition(adapter.getItemCount()-1);
    }

    public void addNewComment() {
//        DialogInfo dialogInfo = (DialogInfo) bundle.getSerializable("new_comment");
//        listMessages.add(dialogInfo);
//        listView.smoothScrollToPosition(adapter.getItemCount() - 1);
//        adapter.notifyDataSetChanged();
    }

}
