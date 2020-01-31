package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.DialogInfo;
import com.project.scratchstudio.kith_andoid.UI.Comments.DialogFragment;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class NewCommentFragment  extends Fragment {
    private static long buttonCount = 0;
    private Bundle bundle;
    private int boardId;

    public NewCommentFragment() {}

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
        setButtonsListener();
        init();
    }

    private void init(){
        CustomFontTextView name = getActivity().findViewById(R.id.name);
        CustomFontTextView position = getActivity().findViewById(R.id.position);
        ImageView photo = getActivity().findViewById(R.id.photo);
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

    private void setButtonsListener(){
        CustomFontTextView cancel = getActivity().findViewById(R.id.cancel);
        cancel.setOnClickListener(this::onClickBack);
        CustomFontTextView send = getActivity().findViewById(R.id.done);
        send.setOnClickListener(this::onClickSend);
    }

    public void onClickBack(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(DialogFragment.newInstance(bundle));
    }

    public boolean onBackPressed() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(DialogFragment.newInstance(bundle));
        return true;
    }

    public void onClickSend(View view){
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        if(isNetworkConnected()) {
            CustomFontEditText user_message = getActivity().findViewById(R.id.comment);
            String message = Objects.requireNonNull(user_message.getText()).toString();
            if (message.length() > 0 ) {
                if(isNetworkConnected()) {
                    CustomFontTextView send = getActivity().findViewById(R.id.done);
                    send.setEnabled(false);
                    HttpService httpService = new HttpService();
                    httpService.sendComment(getActivity(), HomeActivity.getMainUser(), this, boardId, message);
                    user_message.setText("");
                } else Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
    }

    public void createNewComment(String message){
        CustomFontTextView send = getActivity().findViewById(R.id.done);
        send.setEnabled(true);
        DialogInfo newDialog = new DialogInfo();
        newDialog.user_id = HomeActivity.getMainUser().getId();
        newDialog.message = message;
        newDialog.photo = HomeActivity.getMainUser().getUrl();

        bundle.putSerializable("new_comment", newDialog);
        onClickBack(null);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

}
