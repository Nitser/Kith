package com.project.scratchstudio.kith_andoid.UI.UserProfile;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.EditActivity;
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.UserPresenter;
import com.project.scratchstudio.kith_andoid.app.BaseFragment;
import com.project.scratchstudio.kith_andoid.app.Const;
import com.project.scratchstudio.kith_andoid.databinding.FragmentProfileBinding;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi;
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ProfileFragment extends BaseFragment {

    private boolean anoutherUser;
    private User user;
    private UserApi userApi;
    private CompositeDisposable disposable = new CompositeDisposable();
    private View share;
    private FragmentProfileBinding binding;

    private UserPresenter userPresenter;

    public static ProfileFragment newInstance(Bundle bundle) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        user = (User) bundle.getSerializable("user");
        anoutherUser = bundle.getBoolean("another_user");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userPresenter = new UserPresenter(getContext());
        userApi = ApiClient.getClient(getContext()).create(UserApi.class);

        if (anoutherUser) {
            binding.edit.setVisibility(View.GONE);
            binding.exit.setVisibility(View.GONE);

            binding.myShare.setVisibility(View.GONE);
            binding.share.setVisibility(View.VISIBLE);
        }

        fullField();
        initButtonListener();
    }

    private void fullField() {

        if (user.photo != null) {
            Picasso.with(getContext()).load(user.photo.replaceAll("@[0-9]*", ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding.photo);
        }

        binding.name.setText(user.getFirstName());
        binding.surname.setText(user.getLastName());
        binding.phone.setText(user.getPhone());
        if (user.getMiddleName() != null && !user.getMiddleName().equals("") && !user.getMiddleName().toLowerCase().equals("null")) {
            binding.middlename.setText(user.getMiddleName());
        } else {
            binding.middlename.setText("-");
        }
        binding.position.setText(user.getPosition());
        binding.usersCount.setText(String.valueOf(user.getUsersCount()));
        if (user.getDescription() == null || user.getDescription().equals("null") || user.getDescription().equals("")) {
            binding.description.setText("-");
        } else {
            binding.description.setText(user.getDescription());
        }

        if (user.getEmail() == null || user.getEmail().equals("null") || user.getEmail().equals("")) {
            binding.email.setText("-");
        } else {
            binding.email.setText(user.getEmail());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            share.setEnabled(true);
        }
        if (requestCode == 1) {
            if (data != null && resultCode == 0) {
                userPresenter.getUser(new UserPresenter.GetUserCallback() {
                    @Override
                    public void onSuccess(final UserResponse userResponse) {
                        if(userResponse.getUser().photo != null){
                            HomeActivity.getMainUser().photo = (userResponse.getUser().photo);
                            Log.i("Profile", userResponse.getUser().photo);
                        } else {
                            Log.i("Profile", "null photo");
                        }
                        refreshUser(userResponse.getUser());
                        Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(final NetworkErrorException networkError) {
                        Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                    }
                }, HomeActivity.getMainUser().id);
            }
        }
    }

    private void refreshUser(User refUser) {
        if (refUser.photo != null) {
            Picasso.with(getContext()).load(refUser.photo.replaceAll("@[0-9]*", ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding.photo);
        }
        binding.name.setText(refUser.getFirstName());
        binding.surname.setText(refUser.getLastName());
        binding.phone.setText(refUser.getPhone());
        if (refUser.getMiddleName() != null && !refUser.getMiddleName().equals("") && !refUser.getMiddleName().toLowerCase().equals("null")) {
            binding.middlename.setText(refUser.getMiddleName());
        } else {
            binding.middlename.setText("-");
        }
        binding.position.setText(refUser.getPosition());
        binding.usersCount.setText(String.valueOf(refUser.getUsersCount()));
        if (refUser.getDescription() == null || refUser.getDescription().equals("") || refUser.getDescription().toLowerCase().equals("null")) {
            binding.description.setText("-");
        } else {
            binding.description.setText(refUser.getDescription());
        }
        if (refUser.getEmail() == null || refUser.getEmail().equals("null") || refUser.getEmail().equals("")) {
            binding.email.setText("-");
        } else {
            binding.email.setText(refUser.getEmail());
        }
    }

    private void initButtonListener() {
        binding.back.setOnClickListener(this::onClickBack);
        binding.exit.setOnClickListener(this::onClickExitButton);
        binding.edit.setOnClickListener(this::onClickEditButton);
        binding.share.setOnClickListener(this::onClickContactShare);
        binding.myShare.setOnClickListener(this::onClickContactShare);
        binding.phone.setOnClickListener(this::onClickCallPhone);
    }

    private void onClickBack(View view) {
        ((HomeActivity) getActivity()).backFragment();
    }

    private void onClickExitButton(View view) {
        view.setEnabled(false);
        ((HomeActivity) getActivity()).exit();
    }

    private void onClickEditButton(View view) {
        view.setEnabled(false);
        Intent intent = new Intent(getActivity(), EditActivity.class);
        startActivityForResult(intent, 1);
        view.setEnabled(true);
    }

    private void onClickCallPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + user.getPhone()));
        startActivity(intent);
    }

    private void onClickContactShare(View view) {
        view.setEnabled(false);
        share = view;
        disposable.add(
                userApi.getReferralCode(HomeActivity.getMainUser().id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ReferralResponse>() {
                            @Override
                            public void onSuccess(ReferralResponse response) {
                                Log.i("REf", response.getStatus() + " " + response.getReferral());
                                String result = "ФИО: " + binding.surname.getText() + " " + binding.name.getText() + " " + binding.middlename
                                        .getText() + "\n"
                                        + (binding.phone.getText().length() > 1 ? "Телефон: " + binding.phone.getText() + "\n" : "")
                                        + (binding.email.getText().length() > 1 ? "Эл.почта: " + binding.email.getText() + "\n" : "")
                                        + (user.position.length() > 1 ? "Сфера деятельности: " + user.position + "\n" : "")
                                        + "Приложение: " + Const.APP_URL + "\n"
                                        + "Реф.код для регистрации: " + response.getReferral() + "\n"
                                        + getString(R.string.signature);

                                shareDate(result);
                            }

                            @Override
                            public void onError(Throwable e) {
                                String result = "ФИО: " + binding.surname.getText() + " " + binding.name.getText() + " " + binding.middlename
                                        .getText() + "\n"
                                        + "Телефон: " + binding.phone.getText() + "\n"
                                        + "Эл.почта: " + binding.email.getText() + "\n"
                                        + "Сфера деятельности: " + user.position + "\n"
                                        + "Приложение: " + Const.APP_URL + "\n"
                                        + getString(R.string.signature);

                                shareDate(result);
                            }
                        })
        );
    }

    private void shareDate(String share) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, share);
        sendIntent.setType("text/plain");
        startActivityForResult(sendIntent, 0);
    }

    @Override
    public boolean onBackPressed() {
        return ((HomeActivity) getActivity()).backFragment();
    }

}
