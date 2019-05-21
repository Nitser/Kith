package com.project.scratchstudio.kith_andoid.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.CodeActivity;
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.TreeService;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private RecyclerView list;
    private User chosenUser;
    public SearchAdapter adapter;
    public static boolean done = false;

    private static List<SearchInfo> listPersons  = new ArrayList<>();

    public static void setListPersons(List<SearchInfo> list) { listPersons = list; }

    public static SearchFragment newInstance(Bundle bundle) {
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setButtonsListener();
        HttpService httpService = new HttpService();
        httpService.searchUsers(getActivity(), HomeActivity.getMainUser(), this);

        list = getActivity().findViewById(R.id.listPerson);

        EditText filter = getActivity().findViewById(R.id.filter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (SearchFragment.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(llm);

    }

    public void setAdapter(){
        adapter = new SearchAdapter(getActivity(), listPersons, item -> {
            if (SystemClock.elapsedRealtime() - buttonCount < 1000){
                return;
            }
            buttonCount = SystemClock.elapsedRealtime();
//            view.setEnabled(false);

            User user = new User();
            user.setId(item.id);
            user.setFirstName(item.firstName);
            user.setLastName(item.lastName);
            user.setPosition(item.position);
            user.setUrl(item.photo);

            Bundle bundle = new Bundle();
            bundle.putBoolean("another_user", true);
            bundle.putSerializable("user", user);
            HomeActivity.getStackBundles().add(bundle);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(TreeFragment.newInstance(bundle));
//            view.setEnabled(true);
        });
        list.setAdapter(adapter);
    }

    private void setButtonsListener(){
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        ImageButton code = getActivity().findViewById(R.id.plus);
        code.setOnClickListener(this::onClickCode);
    }

    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

    public void onClickBack(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(TreeFragment.newInstance(bundle));
    }

    public void onClickCode(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        Intent intent = new Intent(getContext(), CodeActivity.class);
        startActivity(intent);
        view.setEnabled(true);
    }
}
