package com.project.scratchstudio.kith_andoid.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.CodeActivity;
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Activities.ProfileActivity;
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.Adapters.TreeAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Holders.TreeHolder;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TreeFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private HttpService httpService;

    private LinearLayout linearLayout;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private User currentUser;

    public TreeFragment() {}

    public static TreeFragment newInstance(Bundle bundle) {
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

        if(bundle!= null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")){
            currentUser = mainUser;
            if(currentUser.getFirstName() == null){
                httpService.getUser(getActivity());
            } else {
                userName = currentUser.getFirstName() + " " + currentUser.getLastName();
            }
            HomeActivity.createInvitedUsers();
        } else if(bundle != null && bundle.containsKey("user")){
            currentUser = (User)bundle.getSerializable("user");
            userName = currentUser.getFirstName() + " " + currentUser.getLastName();

            ImageButton back = getActivity().findViewById(R.id.back);
            back.setVisibility(View.VISIBLE);
        }

        init(userName);
        httpService.referralCount(getActivity(), currentUser, false);
        httpService.referralCount(getActivity(), currentUser, true);
        httpService.getInvitedUsers(getActivity(), currentUser, this);

        mySwipeRefreshLayout = getActivity().findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this::myUpdateOperation);

    }

    private void init(String str_name){
        CustomFontTextView name = getActivity().findViewById(R.id.name);
        CustomFontTextView position = getActivity().findViewById(R.id.position);
        ImageView photo = getActivity().findViewById(R.id.photo);
        if(currentUser.getUrl() != null)
            Picasso.with(getActivity()).load(currentUser.getUrl().replaceAll("@[0-9]*", ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .into(photo);
        name.setText(str_name);
        position.setText(currentUser.getPosition());

        linearLayout = getActivity().findViewById(R.id.list_view);

    }

    public void setInvitedUsersList(List<User> invitedUsers) {
        cleanUsers();
        for (int i = 0; i < invitedUsers.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View itemView = inflater.inflate(R.layout.list_item_layout, null);
            linearLayout.addView(itemView, i);
            TreeHolder holder = new TreeHolder(itemView);

            User user = invitedUsers.get(i);
            String name = user.getFirstName() + " " + user.getLastName();
            holder.name.setText(name);
            holder.position.setText(user.getPosition());

            if (user.getUrl() != null && !user.getUrl().equals("null") && !user.getUrl().equals("")) {
                Picasso.with(getActivity()).load(user.getUrl())
                        .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                        .transform(new PicassoCircleTransformation())
                        .into(holder.image);
                holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                homeActivity.loadFragment(TreeFragment.newInstance(bundle));
//            view.setEnabled(true);
            });
        }
    }

    private void cleanUsers(){
        linearLayout.removeAllViews();
    }

    private void myUpdateOperation(){
        Activity activity = getActivity();

        if(isNetworkConnected()){
            User mainUser = HomeActivity.getMainUser();
            new Handler().post(() -> {
                if( bundle!= null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")){
                    httpService.referralCount(activity, mainUser,false);
                    httpService.referralCount(activity, mainUser,true);

                    HomeActivity.createInvitedUsers();
                    httpService.refreshInvitedUsers(activity, mainUser.getId(), true, mySwipeRefreshLayout, this);
                } else {
                    User user = (User)bundle.getSerializable("user");
                    httpService.referralCount(activity, user,true);
                    httpService.referralCount(getActivity(), user,false);
                    httpService.refreshInvitedUsers(activity, user.getId(), false, mySwipeRefreshLayout, this);
                }
            });
        } else {
            Toast.makeText(activity, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

    private void setButtonsListener(){
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        ImageButton search = getActivity().findViewById(R.id.search);
        search.setOnClickListener(this::onClickSearch);
        ImageButton code = getActivity().findViewById(R.id.plus);
        code.setOnClickListener(this::onClickCode);
        ImageButton profile = getActivity().findViewById(R.id.buttonProfile);
        profile.setOnClickListener(this::onClickProfileButton);
    }

    public void onClickBack(View view) {
        HomeActivity.getStackBundles().remove(HomeActivity.getStackBundles().size()-1);
        Bundle bundle = HomeActivity.getStackBundles().get(HomeActivity.getStackBundles().size()-1);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(TreeFragment.newInstance(bundle));
    }

    public boolean onBackPressed() {
        if (HomeActivity.getStackBundles().size() == 1) {
            getActivity().finish();
            return true;
        } else {
            HomeActivity.getStackBundles().remove(HomeActivity.getStackBundles().size() - 1);
            Bundle bundle = HomeActivity.getStackBundles().get(HomeActivity.getStackBundles().size() - 1);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.loadFragment(TreeFragment.newInstance(bundle));
            return true;
        }
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

    public void onClickSearch(View view) {
        ((HomeActivity)view.getContext()).loadFragment( SearchFragment.newInstance(bundle));
    }

    public void onClickProfileButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("another_user", true);
        if(bundle!= null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")){
            intent.putExtra("user", HomeActivity.getMainUser());
        } else if(bundle != null && bundle.containsKey("user")){
            User user = (User)bundle.getSerializable("user");
            intent.putExtra("user", user);
        }

        startActivity(intent);
        view.setEnabled(true);
    }

}
