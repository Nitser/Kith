package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Adapters.AnnouncementAdapter;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private RecyclerView container;
    private AnnouncementAdapter adapter;

    private static List<AnnouncementInfo> listAnn  = new ArrayList<>();

    public static void setListAnn( List<AnnouncementInfo> list ) {
        listAnn = list;
    }

    public AnnouncementFragment() {}

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
        HttpService httpService = new HttpService();
        httpService.getAnnouncements(getActivity(), HomeActivity.getMainUser(), this);

        container = getActivity().findViewById(R.id.listCards);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        container.setLayoutManager(llm);

    }

    public void setAdapter(){
        adapter = new AnnouncementAdapter(getActivity(), listAnn, item -> {
            Bundle newBundle = new Bundle();
            newBundle.putSerializable("info", item);
            ((HomeActivity)getContext()).loadFragment( AnnouncementInfoFragment.newInstance(newBundle));
        });
        container.setAdapter(adapter);
    }

    private void setButtonsListener(){
        ImageButton back = getActivity().findViewById(R.id.plus);
        back.setOnClickListener(this::onClickAdd);

        Button all = getActivity().findViewById(R.id.all);
        Button sub = getActivity().findViewById(R.id.sub);
        Button my = getActivity().findViewById(R.id.my);

        all.setOnClickListener(this::onClickAll);
        sub.setOnClickListener(this::onClickSub);
        my.setOnClickListener(this::onClickMy);
    }

    public void onClickAll(View view){
        HttpService httpService = new HttpService();
        httpService.getAnnouncements(getActivity(), HomeActivity.getMainUser(), this);
    }

    public void onClickSub(View view){
        HttpService httpService = new HttpService();
        httpService.getSubscribedAnnouncement(getActivity(), HomeActivity.getMainUser(), this, false);
    }

    public void onClickMy(View view){
        HttpService httpService = new HttpService();
        httpService.getMyAnnouncement(getActivity(), HomeActivity.getMainUser(), this);
    }

    public void onClickAdd(View view){
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(NewAnnouncementFragment.newInstance(bundle));
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }
}
