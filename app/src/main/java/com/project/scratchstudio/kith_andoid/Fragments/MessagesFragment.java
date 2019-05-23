package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Adapters.MessageDialogAdapter;
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.Model.MessageDialogInfo;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private RecyclerView listView;
    private MessageDialogAdapter adapter;

    private static List<MessageDialogInfo> listMessages  = new ArrayList<>();

    public MessagesFragment() {}

    public static MessagesFragment newInstance(Bundle bundle) {
        MessagesFragment fragment = new MessagesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setButtonsListener();
        listView = getActivity().findViewById(R.id.listMessages);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);

    }

    public void setAdapter(){
        adapter = new MessageDialogAdapter(getActivity(), listMessages, item -> {
            if (SystemClock.elapsedRealtime() - buttonCount < 1000){
                return;
            }
            buttonCount = SystemClock.elapsedRealtime();
//            view.setEnabled(false);

            //открытие диалога
//            view.setEnabled(true);
        });
        listView.setAdapter(adapter);
    }

    private void setButtonsListener(){
//        ImageButton back = getActivity().findViewById(R.id.back);
//        back.setOnClickListener(this::onClickBack);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

}
