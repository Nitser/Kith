package com.project.scratchstudio.kith_andoid.Fragments;

import android.accounts.NetworkErrorException;
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
import android.util.Log;
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
import com.project.scratchstudio.kith_andoid.Activities.MainActivity;
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Holders.TreeHolder;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken;
import com.project.scratchstudio.kith_andoid.UserPresenter;
import com.project.scratchstudio.kith_andoid.app.FragmentType;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.network.model.user.UserListResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TreeFragment extends Fragment {

    private static long buttonCount = 0;
    private Bundle bundle;
    private HttpService httpService;
    private SearchAdapter searchAdapter;

    private RecyclerView list;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private User currentUser = HomeActivity.getMainUser();

    private UserApi userApi;
    private CompositeDisposable disposable = new CompositeDisposable();
    private UserPresenter userPresenter;

    private static List<User> listPersons = new ArrayList<>();

    private static void setListPersons(List<User> list) {
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
        userPresenter = new UserPresenter(getContext());
        userApi = ApiClient.getClient(getContext()).create(UserApi.class);

        setButtonsListener();
        httpService = new HttpService();
        User mainUser = HomeActivity.getMainUser();
        String userName = "";

        if (mainUser.getFirstName() == null) {
            userPresenter.getUser(new UserPresenter.GetUserCallback() {
                @Override
                public void onSuccess(final UserResponse userResponse) {
                    if (userResponse.getStatus()) {
                        currentUser.firstName = userResponse.getUser().firstName;
                        currentUser.lastName = userResponse.getUser().lastName;
                        currentUser.middleName = userResponse.getUser().middleName;
                        currentUser.login = userResponse.getUser().login;
                        currentUser.phone = userResponse.getUser().phone;
                        currentUser.description = userResponse.getUser().description;
                        currentUser.position = userResponse.getUser().position;
                        currentUser.email = userResponse.getUser().email;
                        currentUser.photo = userResponse.getUser().photo.replaceAll("\\/", "/");

                        ImageView photo = view.findViewById(R.id.photo);
                        Picasso.with(getContext()).load(currentUser.photo)
                                .placeholder(R.mipmap.person)
                                .error(R.mipmap.person)
                                .transform(new PicassoCircleTransformation())
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .into(photo);

                        CustomFontTextView name = view.findViewById(R.id.name);
                        CustomFontTextView position = view.findViewById(R.id.position);
                        String userName = currentUser.getFirstName() + " " + currentUser.getLastName();
                        name.setText(userName);
                        position.setText(currentUser.getPosition());
                    } else {
                        Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                        exit();
                    }
                }

                @Override
                public void onError(final NetworkErrorException networkError) {
                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                    exit();
                }
            }, currentUser.id);
        } else {
            userName = currentUser.getFirstName() + " " + currentUser.getLastName();
        }
        HomeActivity.createInvitedUsers();

        init(userName);
        if (isNetworkConnected()) {
            httpService.referralCount(getActivity(), currentUser, false);
            httpService.referralCount(getActivity(), currentUser, true);
        } else {
            Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        }

        userPresenter.getInvitedUsers(new UserPresenter.GetUserListCallback() {
            @Override
            public void onSuccess(final User[] userResponse) {
                List<User> response = new ArrayList<>(Arrays.asList(userResponse));
                for (User user : response) {
                    if (user.photo != null) {
                        user.photo = user.photo.replaceAll("\\/", "/").replaceAll("@[0-9]*", "");
                        Log.d("UserDebug", "Photo: " + user.photo);
                    }
                }
                setInvitedUsersList(response);
            }

            @Override
            public void onError(final NetworkErrorException networkError) {
                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
            }
        }, HomeActivity.getMainUser().id);

        mySwipeRefreshLayout = getActivity().findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this::myUpdateOperation);

    }

    private void exit() {
        HomeActivity.cleanMainUser();
        InternalStorageService internalStorageService = new InternalStorageService(getActivity());
        internalStorageService.setiSetInternalData(new ClearUserIdAndToken());
        internalStorageService.execute();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    private void init(String str_name) {
        CustomFontTextView name = getActivity().findViewById(R.id.name);
        CustomFontTextView position = getActivity().findViewById(R.id.position);
        ImageView photo = getActivity().findViewById(R.id.photo);
        if (currentUser.photo != null) {
            Picasso.with(getActivity()).load(currentUser.photo.replaceAll("@[0-9]*", ""))
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

    private void setInvitedUsersList(List<User> invitedUsers) {
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

            Picasso.with(getActivity()).load(user.photo)
                    .placeholder(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                    .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image);

            itemView.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
                    return;
                }
                buttonCount = SystemClock.elapsedRealtime();
                userPresenter.openProfile(user);
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
                    userPresenter.getInvitedUsers(new UserPresenter.GetUserListCallback() {
                        @Override
                        public void onSuccess(final User[] userResponse) {
                            List<User> response = new ArrayList<>(Arrays.asList(userResponse));
                            for (User user : response) {
                                if (user.photo != null) {
                                    user.photo = user.photo.replaceAll("\\/", "/").replaceAll("@[0-9]*", "");
                                }
                            }
                            setInvitedUsersList(response);
                            mySwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(final NetworkErrorException networkError) {
                            Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }, HomeActivity.getMainUser().id);

                    refreshUser();
                }
            });
        } else {
            Toast.makeText(activity, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refreshUser() {
        ImageView photo = getActivity().findViewById(R.id.photo);
        CustomFontTextView name = getActivity().findViewById(R.id.name);
        CustomFontTextView position = getActivity().findViewById(R.id.position);

        User user = HomeActivity.getMainUser();
        Picasso.with(getActivity()).load(user.photo.replaceAll("@[0-9]*", ""))
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

    private void onClickBack(View view) {
        HomeActivity.getStackBundles().remove(HomeActivity.getStackBundles().size() - 1);
        Bundle bundle = HomeActivity.getStackBundles().get(HomeActivity.getStackBundles().size() - 1);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.replaceFragment(TreeFragment.newInstance(bundle, FragmentType.TREE.name()), FragmentType.TREE.name());
    }

    private void onClickCode(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        Intent intent = new Intent(getContext(), CodeActivity.class);
        startActivity(intent);
        view.setEnabled(true);
    }

    private void onClickSearch(View view) {
        RelativeLayout layout = getActivity().findViewById(R.id.search_header);
        layout.setVisibility(View.VISIBLE);
        LinearLayout linearLayout = getActivity().findViewById(R.id.paper_search);
        EditText editText = getActivity().findViewById(R.id.filter);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        view.setEnabled(false);
        disposable.add(
                userApi.searchUsers(HomeActivity.getMainUser().id, "", "0", "500")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserListResponse>() {
                            @Override
                            public void onSuccess(UserListResponse response) {
                                if (response.getStatus()) {
                                    for (User user : response.getUsers()) {
                                        if (user.photo != null) {
                                            user.photo = user.photo.replaceAll("\\/", "/").replaceAll("@[0-9]*", "");
                                        }
                                    }
                                    setListPersons(response.getUsers());
                                    setSearchAdapter(view);
                                } else {
                                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                }
                                view.setEnabled(true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("TreeFragmentInfo", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                view.setEnabled(true);
                            }
                        })
        );

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

    private void onClickBackSearch(View view) {
        RelativeLayout layout = getActivity().findViewById(R.id.search_header);
        layout.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout = getActivity().findViewById(R.id.paper_search);
        linearLayout.setVisibility(View.INVISIBLE);
        EditText editText = getActivity().findViewById(R.id.filter);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void setSearchAdapter(View view) {
        searchAdapter = new SearchAdapter(getActivity(), listPersons, item -> getProfile(view, item.id));
        list.setAdapter(searchAdapter);
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void getProfile(View view, int newUserId) {
        view.setEnabled(false);
        EditText editText = getActivity().findViewById(R.id.filter);
        editText.requestFocus();
        userPresenter.getUser(new UserPresenter.GetUserCallback() {
            @Override
            public void onSuccess(final UserResponse userResponse) {
                if (userResponse.getStatus()) {
                    userResponse.getUser().setId(newUserId);
                    if (userResponse.getUser().photo != null) {
                        userResponse.getUser().photo = (userResponse.getUser().photo.replaceAll("\\/", "/"));
                    }
                    hideKeyboard(getActivity());
                    userPresenter.openProfile(userResponse.getUser());
                } else {
                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                }
                view.setEnabled(true);
            }

            @Override
            public void onError(final NetworkErrorException networkError) {
                Log.e("TreeFragmentInfo", "onError: " + networkError.getMessage());
                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        }, newUserId);
    }

    private void onClickProfileButton(View view) {
        view.setEnabled(false);
        userPresenter.openProfile(HomeActivity.getMainUser());
        view.setEnabled(true);
    }

}
