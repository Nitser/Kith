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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Adapters.MessageDialogAdapter;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.UI.Comments.DialogFragment;

public class MessagesFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private RecyclerView listView;
    private MessageDialogAdapter adapter;

//    private static List<MessageDialogInfo> listMessages  = new ArrayList<>();

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
        listView = getActivity().findViewById(R.id.listMessages);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);

        if(HomeActivity.getBoardsList().size() == 0) {
            HttpService httpService = new HttpService();
//            httpService.getSubscribedAnnouncement(getActivity(), HomeActivity.getMainUser(), this, true);
        } else
            setAdapter();

        setButtonsListener();

        EditText filter = getActivity().findViewById(R.id.filter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (MessagesFragment.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setAdapter(){
        adapter = new MessageDialogAdapter(getActivity(), HomeActivity.getBoardsList(), item -> {
            if (SystemClock.elapsedRealtime() - buttonCount < 1000){
                return;
            }
            buttonCount = SystemClock.elapsedRealtime();
//            view.setEnabled(false);

            //открытие диалога
            Bundle bundle = new Bundle();
            bundle.putInt("board_id", item.id);
            bundle.putString("board_title", item.title);
//            HomeActivity.getStackBundles().add(bundle);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.replaceFragment(DialogFragment.newInstance(bundle));

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

    public boolean onBackPressed() {
        return true;
    }


}
