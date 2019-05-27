package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Adapters.DialogAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Holders.DialogHolder;
import com.project.scratchstudio.kith_andoid.Model.DialogInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;

import net.mrbin99.laravelechoandroid.Echo;
import net.mrbin99.laravelechoandroid.EchoCallback;
import net.mrbin99.laravelechoandroid.EchoOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogFragment extends Fragment {
    private static long buttonCount = 0;
    private Bundle bundle;
    private Echo echo;
    private int boardId;
    private String boardTitle;
    private RecyclerView listView;
    private DialogAdapter adapter;

    private static List<DialogInfo> listMessages  = new ArrayList<>();

    public static void setListMessages ( List<DialogInfo> list ) { listMessages = list; }

    public DialogFragment() {}

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
        HttpService httpService = new HttpService();
        httpService.getComments(getActivity(), HomeActivity.getMainUser(), this, boardId);

        setButtonsListener();

        listView = getActivity().findViewById(R.id.listDialog);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        listView.setLayoutManager(llm);
        CustomFontTextView title = getActivity().findViewById(R.id.title);
        title.setText(boardTitle);
        setNewMessageListener();
    }

    public void setNewMessageListener(){
        EchoOptions options = new EchoOptions();

        options.host = "http://dev.kith.ml:6001";
        options.headers.put("Authorization", HomeActivity.getMainUser().getToken());

        echo = new Echo(options);
        echo.connect(args -> {
            // Success connect
            Log.i("ECHO", "ok");
        }, args -> {
            // Error connect
            Log.i("ECHO", "error");
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        echo.disconnect();
    }

    public void setAdapter(){
        adapter = new DialogAdapter(getActivity(), listMessages, item -> {
            if (SystemClock.elapsedRealtime() - buttonCount < 1000){
                return;
            }
            buttonCount = SystemClock.elapsedRealtime();
//            view.setEnabled(false);

//            view.setEnabled(true);
        });
        listView.smoothScrollToPosition(adapter.getItemCount()-1);
        listView.setAdapter(adapter);
    }

    private void setButtonsListener(){
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        ImageButton send = getActivity().findViewById(R.id.send);
        send.setOnClickListener(this::onClickSend);
    }

    public void onClickBack(View view) {
        Bundle bundle = new Bundle();
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(MessagesFragment.newInstance(bundle));
    }

    public void onClickSend(View view){
        CustomFontEditText user_message = getActivity().findViewById(R.id.user_message);
        String message = Objects.requireNonNull(user_message.getText()).toString();
//                .replaceAll("\\s+", "");
        if(message.length()>0){
            HttpService httpService = new HttpService();
            httpService.sendComment(getActivity(), HomeActivity.getMainUser(), this, boardId, message);
            user_message.setText("");
        }
    }

    public void createNewComment(String message){
        DialogInfo newDialog = new DialogInfo();
        newDialog.user_id = HomeActivity.getMainUser().getId();
        newDialog.message = message;
        newDialog.photo = HomeActivity.getMainUser().getUrl();

        adapter.newItem = true;
        listMessages.add(newDialog);
        listView.smoothScrollToPosition(adapter.getItemCount()-1);
        adapter.notifyDataSetChanged();
//        listView.scrollToPosition(adapter.getItemCount()-1);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

}
