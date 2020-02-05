package com.project.scratchstudio.kith_andoid.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.CodeActivity;
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Activities.ProfileActivity;
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Holders.TreeHolder;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.app.FragmentType;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TreeFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private HttpService httpService;
    public SearchAdapter searchAdapter;

    private RecyclerView list;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private User currentUser;

    private static List<SearchInfo> listPersons = new ArrayList<>();

    public static void setListPersons(List<SearchInfo> list) {
        listPersons = list;
    }

    public TreeFragment() {
    }

    public static TreeFragment newInstance(Bundle bundle, String title) {
        TreeFragment treeFragment = new TreeFragment();
        treeFragment.setArguments(bundle);
        return treeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        return inflater.inflate(R.layout.fragment_tree, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        setButtonsListener();
        httpService = new HttpService();
        User mainUser = HomeActivity.getMainUser();
        String userName = "";

        if (bundle != null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")) {
            currentUser = mainUser;
            if (currentUser.getFirstName() == null) {
                if (isNetworkConnected()) {
                    httpService.getUser(getActivity(), false);
                } else {
                    Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                }
            } else {
                userName = currentUser.getFirstName() + " " + currentUser.getLastName();
            }
            HomeActivity.createInvitedUsers();
        } else if (bundle != null && bundle.containsKey("user")) {
            currentUser = (User) bundle.getSerializable("user");
            userName = currentUser.getFirstName() + " " + currentUser.getLastName();

            ImageButton back = getActivity().findViewById(R.id.back);
            back.setVisibility(View.VISIBLE);
        }

        init(userName);
        if (isNetworkConnected()) {
            httpService.referralCount(getActivity(), currentUser, false);
            httpService.referralCount(getActivity(), currentUser, true);
            httpService.getInvitedUsers(getActivity(), currentUser, this);
        } else {
            Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }

        mySwipeRefreshLayout = getActivity().findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this::myUpdateOperation);

    }

    private void init(String str_name) {
        CustomFontTextView name = getActivity().findViewById(R.id.name);
        CustomFontTextView position = getActivity().findViewById(R.id.position);
        ImageView photo = getActivity().findViewById(R.id.photo);
        if (currentUser.getUrl() != null) {
            Picasso.with(getActivity()).load(currentUser.getUrl().replaceAll("@[0-9]*", ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo);
        }
        name.setText(str_name);
        position.setText(currentUser.getPosition());

        linearLayout = getActivity().findViewById(R.id.list_view);

    }

    public void setInvitedUsersList(List<User> invitedUsers) {
        cleanUsers();
        for (int i = 0; i < invitedUsers.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.list_item_layout, null);
            linearLayout.addView(itemView, i);
            TreeHolder holder = new TreeHolder(itemView);

            User user = invitedUsers.get(i);
            String name = user.getFirstName() + " " + user.getLastName();
            holder.name.setText(name);
            holder.position.setText(user.getPosition());

            if (user.getUrl() != null && !user.getUrl().equals("null") && !user.getUrl().equals("")) {
                Picasso.with(getActivity()).load(user.getUrl().replaceAll("@[0-9]*", ""))
                        .placeholder(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                        .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                        .transform(new PicassoCircleTransformation())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(holder.image);
            }

            itemView.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
                    return;
                }
                buttonCount = SystemClock.elapsedRealtime();
//            view.setEnabled(false);

                Bundle bundle = new Bundle();
                bundle.putBoolean("another_user", true);
                bundle.putSerializable("user", user);
                HomeActivity.getStackBundles().add(bundle);
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(TreeFragment.newInstance(bundle, FragmentType.TREE.name()), FragmentType.TREE.name());
//            view.setEnabled(true);
            });
        }
    }

    private void cleanUsers() {
        linearLayout.removeAllViews();
    }

    private void myUpdateOperation() {
        Activity activity = getActivity();

        if (isNetworkConnected()) {
            User mainUser = HomeActivity.getMainUser();
            new Handler().post(() -> {
                if (bundle != null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")) {
                    httpService.referralCount(activity, mainUser, false);
                    httpService.referralCount(activity, mainUser, true);

                    HomeActivity.createInvitedUsers();
                    httpService.refreshInvitedUsers(activity, mainUser.getId(), true, mySwipeRefreshLayout, this);

                    refreshUser();

                } else {
                    User user = (User) bundle.getSerializable("user");
                    httpService.referralCount(activity, user, true);
                    httpService.referralCount(getActivity(), user, false);
                    httpService.refreshInvitedUsers(activity, user.getId(), false, mySwipeRefreshLayout, this);
                }
            });
        } else {
            Toast.makeText(activity, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void refreshUser() {
        ImageView photo = getActivity().findViewById(R.id.photo);
        CustomFontTextView name = getActivity().findViewById(R.id.name);
        CustomFontTextView position = getActivity().findViewById(R.id.position);

        User user = HomeActivity.getMainUser();
        Picasso.with(getActivity()).load(user.getUrl().replaceAll("@[0-9]*", ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(new PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(photo);
        String nameStr = HomeActivity.getMainUser().getFirstName() + " " + HomeActivity.getMainUser().getLastName();
        name.setText(nameStr);
        position.setText(user.getPosition());
    }

    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null);
    }

    private void setButtonsListener() {
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        ImageButton search = getActivity().findViewById(R.id.search);
        search.setOnClickListener(this::onClickSearch);
        ImageButton code = getActivity().findViewById(R.id.plus);
        code.setOnClickListener(this::onClickCode);
        ImageButton profile = getActivity().findViewById(R.id.buttonProfile);
        profile.setOnClickListener(this::onClickProfileButton);
        ImageButton searchBack = getActivity().findViewById(R.id.back_search);
        searchBack.setOnClickListener(this::onClickBackSearch);
    }

    public void onClickBack(View view) {
        HomeActivity.getStackBundles().remove(HomeActivity.getStackBundles().size() - 1);
        Bundle bundle = HomeActivity.getStackBundles().get(HomeActivity.getStackBundles().size() - 1);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.replaceFragment(TreeFragment.newInstance(bundle, FragmentType.TREE.name()), FragmentType.TREE.name());
    }

    public boolean onBackPressed() {
        if (HomeActivity.getStackBundles().size() == 1) {
            getActivity().finish();
            return true;
        } else {
            HomeActivity.getStackBundles().remove(HomeActivity.getStackBundles().size() - 1);
            Bundle bundle = HomeActivity.getStackBundles().get(HomeActivity.getStackBundles().size() - 1);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.replaceFragment(TreeFragment.newInstance(bundle, FragmentType.TREE.name()), FragmentType.TREE.name());
            return true;
        }
    }

    public void onClickCode(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        Intent intent = new Intent(getContext(), CodeActivity.class);
        startActivity(intent);
        view.setEnabled(true);
    }

    public void onClickSearch(View view) {
        RelativeLayout layout = getActivity().findViewById(R.id.search_header);
        layout.setVisibility(View.VISIBLE);
        LinearLayout linearLayout = getActivity().findViewById(R.id.paper_search);
//        linearLayout.setVisibility(View.VISIBLE);
        EditText editText = getActivity().findViewById(R.id.filter);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        if (isNetworkConnected()) {
            httpService.searchUsers(getActivity(), HomeActivity.getMainUser(), this);
        } else {
            Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }

        list = getActivity().findViewById(R.id.listPerson);
        EditText filter = getActivity().findViewById(R.id.filter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
                (TreeFragment.this).searchAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(llm);

    }

    public void onClickBackSearch(View view) {
        RelativeLayout layout = getActivity().findViewById(R.id.search_header);
        layout.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout = getActivity().findViewById(R.id.paper_search);
        linearLayout.setVisibility(View.INVISIBLE);
        EditText editText = getActivity().findViewById(R.id.filter);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setSearchAdapter() {
        searchAdapter = new SearchAdapter(getActivity(), listPersons, item -> {
            if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
                return;
            }
            buttonCount = SystemClock.elapsedRealtime();
//            view.setEnabled(false);

            User user = new User();
            user.setId(item.id);
            user.setFirstName(item.firstName);
            user.setLastName(item.lastName);
            user.setMiddleName(item.middleName);
            user.setPosition(item.position);
            user.setUrl(item.photo);
            user.setPhone(item.phone);
            user.setEmail(item.email);
            user.setDescription(item.description);

            Bundle bundle = new Bundle();
            bundle.putBoolean("another_user", true);
            bundle.putSerializable("user", user);
            HomeActivity.getStackBundles().add(bundle);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.replaceFragment(TreeFragment.newInstance(bundle, FragmentType.TREE.name()), FragmentType.TREE.name());
//            view.setEnabled(true);
        });
        list.setAdapter(searchAdapter);
    }

    public void onClickProfileButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("another_user", true);
        if (bundle != null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")) {
            intent.putExtra("user", HomeActivity.getMainUser());
        } else if (bundle != null && bundle.containsKey("user")) {
            User user = (User) bundle.getSerializable("user");
            intent.putExtra("user", user);
        }

        startActivity(intent);
        view.setEnabled(true);
    }

}
