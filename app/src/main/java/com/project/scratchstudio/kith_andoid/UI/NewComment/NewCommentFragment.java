package com.project.scratchstudio.kith_andoid.UI.NewComment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.DialogInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.app.BaseFragment;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.CommentApi;
import com.project.scratchstudio.kith_andoid.network.model.comment.SendCommentResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NewCommentFragment extends BaseFragment {
    private Bundle bundle;
    private int boardId;

    private CommentApi commentApi;
    private CompositeDisposable disposable = new CompositeDisposable();

    public NewCommentFragment() {
    }

    public static NewCommentFragment newInstance(Bundle bundle) {
        NewCommentFragment fragment = new NewCommentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        boardId = bundle.getInt("board_id");
        return inflater.inflate(R.layout.fragment_new_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        commentApi = ApiClient.getClient(getContext()).create(CommentApi.class);
        setButtonsListener();
        init();
    }

    private void init() {
        CustomFontTextView name = getActivity().findViewById(R.id.new_comment_name);
        CustomFontTextView position = getActivity().findViewById(R.id.new_comment_position);
        ImageView photo = getActivity().findViewById(R.id.new_comment_photo);
        User mainUser = HomeActivity.getMainUser();

        name.setText(mainUser.getFirstName());
        position.setText(mainUser.getPosition());
        Picasso.with(getContext()).load(mainUser.getUrl().replaceAll("@[0-9]*", ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(new PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(photo);
    }

    private void setButtonsListener() {
        CustomFontTextView cancel = getActivity().findViewById(R.id.new_comment_cancel);
        cancel.setOnClickListener(this::onClickBack);
        CustomFontTextView send = getActivity().findViewById(R.id.new_comment_done);
        send.setOnClickListener(this::onClickSend);
    }

    public void onClickBack(View view) {
        ((HomeActivity) getActivity()).backFragment();
    }

    private void onClickSend(View view) {
        CustomFontEditText user_message = getActivity().findViewById(R.id.new_comment_message);
        String message = Objects.requireNonNull(user_message.getText()).toString();
        if (message.trim().length() > 0) {
            CustomFontTextView send = getActivity().findViewById(R.id.new_comment_done);
            send.setEnabled(false);
            disposable.add(
                    commentApi.sendComment(boardId, message)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<SendCommentResponse>() {
                                @Override
                                public void onSuccess(SendCommentResponse response) {
                                    if (response.getStatus()) {
                                        onClickBack(null);
                                    } else {
                                        Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                        send.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e("SendComment Resp", "onError: " + e.getMessage());
                                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                    send.setEnabled(true);
                                }
                            })
            );
        }
    }

    public void createNewComment(String message) {
        CustomFontTextView send = getActivity().findViewById(R.id.done);
        send.setEnabled(true);
        DialogInfo newDialog = new DialogInfo();
        newDialog.user_id = HomeActivity.getMainUser().getId();
        newDialog.message = message;
        newDialog.photo = HomeActivity.getMainUser().getUrl();

        bundle.putSerializable("new_comment", newDialog);
        onClickBack(null);
    }

}
