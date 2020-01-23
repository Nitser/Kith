package com.project.scratchstudio.kith_andoid.Fragments;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.ApiService;
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementInfoFragment extends Fragment {

    private Bundle bundle;
    int boardListId;
    private AnnouncementInfo info;
    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    public void setIsJoin(boolean bol) {
        CheckBox favorite = getActivity().findViewById(R.id.heart);
        favorite.setChecked(bol);
        favorite.setEnabled(true);
    }

    public static AnnouncementInfoFragment newInstance(Bundle bundle) {
        AnnouncementInfoFragment fragment = new AnnouncementInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        bundle.putBoolean("is_edit", false);
        if (bundle != null) {
            boardListId = bundle.getInt("board_list_id");
            info = AnnouncementFragment.getListAnn().get(boardListId);
        }
        return inflater.inflate(R.layout.fragment_announcement_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setButtonsListener();

        TextView title = view.findViewById(R.id.title);
        TextView date = view.findViewById(R.id.date);
        TextView description = view.findViewById(R.id.description);
        TextView owner = view.findViewById(R.id.owner);
        TextView phone = view.findViewById(R.id.phone);
        TextView creationDate = view.findViewById(R.id.creationDate);
        ImageView photo = view.findViewById(R.id.photo);

        title.setText(info.title);
        if (info.endDate.equals("null")) {
            info.endDate = "Неограниченно";
        }

        try {
            DateFormat inputFormat;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date newDateFormat;
                Date newCreationDateFormat = null;
                newDateFormat = inputFormat.parse(info.endDate.replaceAll("\\s.*$", ""));
                if (info.startDate.equals("null")) {
                    creationDate.setVisibility(View.GONE);
                } else {
                    newCreationDateFormat = inputFormat.parse(info.startDate.replaceAll("\\s.*$", ""));
                }
                String outputDateStr = outputFormat.format(newDateFormat);
                date.setText(outputDateStr);
                creationDate.setText(getString(R.string.create_date, outputFormat.format(newCreationDateFormat)));
            } else {
                date.setText(info.endDate.replaceAll("\\s.*$", ""));
                String errDate = "Создано: " + info.startDate.replaceAll("\\s.*$", "");
                creationDate.setText(errDate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String ownerName = "Владелец: ";
        if (info.organizerLastName != null && !info.organizerLastName.equals("null")) {
            ownerName += info.organizerLastName + " ";
        } else {
            owner.setVisibility(View.GONE);
        }
        if (!info.organizerName.equals("null")) {
            owner.setVisibility(View.VISIBLE);
            ownerName += info.organizerName;
        }
        owner.setText(ownerName);

        if (info.organizerPhone != null && !info.organizerPhone.equals("null")) {
            phone.setText(getString(R.string.organizer_phone, info.organizerPhone));
        } else {
            phone.setVisibility(View.GONE);
        }

        if (info.needParticipants.equals("null") || info.needParticipants.equals("")) {
            info.needParticipants = "-";
        }
        description.setText(info.description);
        if (info.url != null && !info.url.equals("null") && !info.url.equals("")) {
            Picasso.with(getActivity()).load(info.url.replaceAll("@[0-9]*", ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if (info.organizerId == HomeActivity.getMainUser().getId()) {
            ImageButton button = getActivity().findViewById(R.id.edit);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(this::onClickEdit);
        }

        if (info.subscriptionOnBoard == 1) {
            setIsJoin(true);
        }

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
    }

    private void setButtonsListener() {
        ImageButton back = getActivity().findViewById(R.id.back);
        back.setOnClickListener(this::onClickBack);
        CheckBox favorite = getActivity().findViewById(R.id.heart);
        favorite.setOnClickListener(this::onClickJoin);
        Button comments = getActivity().findViewById(R.id.comments);
        comments.setOnClickListener(this::onClickComments);
        Button profile = getActivity().findViewById(R.id.user_profile);
        profile.setOnClickListener(this::onClickProfile);
        TextView phone = getActivity().findViewById(R.id.phone);
        phone.setOnClickListener(this::onClickPhone);
    }

    private void onClickPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + info.organizerPhone));
        startActivity(intent);
    }

    public void onClickProfile(View view) {
        view.setEnabled(false);
        disposable.add(
                apiService.getUser(info.organizerId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (response.getStatus()) {
                                    response.getUser().setId(info.organizerId);
                                    response.getUser().setUrl(response.getUser().photo.replaceAll("\\/", "/"));

                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("another_user", true);
                                    bundle.putSerializable("user", response.getUser());
                                    HomeActivity.getStackBundles().add(bundle);
                                    HomeActivity homeActivity = (HomeActivity) getActivity();
                                    homeActivity.loadFragment(TreeFragment.newInstance(bundle));
                                } else {
                                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                    view.setEnabled(true);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("BoardFragmentInfo", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                view.setEnabled(true);
                            }
                        })
        );
    }

    public void onClickEdit(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        bundle.putBoolean("is_edit", true);
        bundle.putSerializable("board_list_id", boardListId);
        homeActivity.loadFragment(NewAnnouncementFragment.newInstance(bundle));
    }

    public void onClickBack(View view) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
    }

    public boolean onBackPressed() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(AnnouncementFragment.newInstance(bundle));
        return true;
    }

    public void onClickComments(View view) {
        bundle.putInt("board_id", info.id);
        bundle.putString("board_title", info.title);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.loadFragment(DialogFragment.newInstance(bundle));
    }

    public void onClickJoin(View view) {
        view.setEnabled(false);
        if (info.subscriptionOnBoard != 1) {
            subscribeAnnouncement(HomeActivity.getMainUser().getId(), info, (CheckBox) view);
        } else {
            unsubscribeAnnouncement(HomeActivity.getMainUser().getId(), info, (CheckBox) view);
        }
    }

    public void subscribeAnnouncement(int user_id, AnnouncementInfo board, CheckBox favorite) {
        disposable.add(
                apiService.subscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FavoriteResponse>() {
                            @Override
                            public void onSuccess(FavoriteResponse response) {
                                if (response.getStatus()) {
                                    board.subscriptionOnBoard = 1;
                                    Toast.makeText(getContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                    favorite.setChecked(!favorite.isChecked());
                                }
                                favorite.setEnabled(true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("AnnouncementFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }

    public void unsubscribeAnnouncement(int user_id, AnnouncementInfo board, CheckBox favorite) {
        disposable.add(
                apiService.unsubscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FavoriteResponse>() {
                            @Override
                            public void onSuccess(FavoriteResponse response) {
                                if (response.getStatus()) {
                                    board.subscriptionOnBoard = 0;
                                    Toast.makeText(getContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                    favorite.setChecked(!favorite.isChecked());
                                }
                                favorite.setEnabled(true);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("AnnouncementFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }
}
