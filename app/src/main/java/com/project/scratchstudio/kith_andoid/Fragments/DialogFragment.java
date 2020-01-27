package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.project.scratchstudio.kith_andoid.Model.DialogInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;

import net.mrbin99.laravelechoandroid.Echo;
import net.mrbin99.laravelechoandroid.EchoOptions;

import java.util.ArrayList;
import java.util.List;

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
        if(isNetworkConnected()) {
            HttpService httpService = new HttpService();
            httpService.getComments(getActivity(), HomeActivity.getMainUser(), this, boardId);
        } else Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();

        setButtonsListener();

        listView = getActivity().findViewById(R.id.listDialog);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        llm.setStackFromEnd(true);
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
//        listView.smoothScrollToPosition(adapter.getItemCount()-1);
        listView.setAdapter(adapter);
    }

    private void setButtonsListener(){
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

    public void onClickSend(View view){
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(NewCommentFragment.newInstance(bundle));
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

    public void addNewComment(){
        DialogInfo dialogInfo = (DialogInfo) bundle.getSerializable("new_comment");
        listMessages.add(dialogInfo);
        listView.smoothScrollToPosition(adapter.getItemCount()-1);
        adapter.notifyDataSetChanged();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

}
