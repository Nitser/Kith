package com.project.scratchstudio.kith_andoid.UI.BoardInfo;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
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
import com.project.scratchstudio.kith_andoid.Activities.ProfileActivity;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.UI.Comments.CommentListFragment;
import com.project.scratchstudio.kith_andoid.UI.NewEditBoard.NewEditBoardFragment;
import com.project.scratchstudio.kith_andoid.app.BaseFragment;
import com.project.scratchstudio.kith_andoid.app.FragmentType;
import com.project.scratchstudio.kith_andoid.network.ApiClient;
import com.project.scratchstudio.kith_andoid.network.apiService.BoardApi;
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi;
import com.project.scratchstudio.kith_andoid.network.model.board.Board;
import com.project.scratchstudio.kith_andoid.network.model.favorite.FavoriteResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class BoardInfoFragment extends BaseFragment {

    private Bundle bundle;
    private Board info;
    private BoardApi boardApi;
    private UserApi userApi;
    private CompositeDisposable disposable = new CompositeDisposable();

    private ImageView photo;

    private void setIsJoin() {
        CheckBox favorite = getActivity().findViewById(R.id.heart);
        favorite.setChecked(true);
        favorite.setEnabled(true);
    }

    public static BoardInfoFragment newInstance(Bundle bundle) {
        BoardInfoFragment fragment = new BoardInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        bundle.putBoolean("is_edit", false);
        if (bundle != null) {
            info = (Board) bundle.getSerializable("board");
        }
        return inflater.inflate(R.layout.fragment_announcement_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setButtonsListener();

        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);
        TextView owner = view.findViewById(R.id.owner);
        TextView phone = view.findViewById(R.id.phone);
        TextView creationDate = view.findViewById(R.id.creationDate);
        photo = view.findViewById(R.id.photo);

        title.setText(info.title);

        try {
            DateFormat inputFormat;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date newCreationDateFormat = null;
                if (info.startDate.equals("null")) {
                    creationDate.setVisibility(View.GONE);
                } else {
                    newCreationDateFormat = inputFormat.parse(info.startDate.replaceAll("\\s.*$", ""));
                }
                creationDate.setText(getString(R.string.create_date, outputFormat.format(newCreationDateFormat)));
            } else {
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
            setIsJoin();
        }

        userApi = ApiClient.getClient(getContext()).create(UserApi.class);
        boardApi = ApiClient.getClient(getContext()).create(BoardApi.class);
    }

    public void reloadPhoto() {
        if (info.url != null && !info.url.equals("null") && !info.url.equals("")) {
            Picasso.with(getActivity()).load(info.url.replaceAll("@[0-9]*", ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
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

    private void onClickProfile(View view) {
        view.setEnabled(false);
        disposable.add(
                userApi.getUser(info.organizerId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (response.getStatus()) {
                                    Log.i("Get User Resp", response.toString());
                                    response.getUser().setId(info.organizerId);
                                    if (response.getUser().photo != null) {
                                        response.getUser().setUrl(response.getUser().photo.replaceAll("\\/", "/"));
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("another_user", true);
                                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                                    intent.putExtra("user", response.getUser());

                                    if (response.getUser().id != HomeActivity.getMainUser().id) {
                                        intent.putExtra("another_user", true);
                                    } else {
                                        intent.putExtra("another_user", false);
                                    }

                                    startActivity(intent);
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

    private void onClickEdit(View view) {
        bundle.putBoolean("is_edit", true);
        bundle.putSerializable("board", info);
        ((HomeActivity) getActivity()).addFragment(NewEditBoardFragment.newInstance(bundle), FragmentType.BOARD_NEW_EDIT.name());
    }

    public void onClickBack(View view) {
        ((HomeActivity) getActivity()).backFragment();
    }

    private void onClickComments(View view) {
        bundle.putInt("board_id", info.id);
        bundle.putString("board_title", info.title);
        ((HomeActivity) getContext()).addFragment(CommentListFragment.newInstance(bundle), FragmentType.COMMENT_LIST.name());
    }

    private void onClickJoin(View view) {
        view.setEnabled(false);
        if (info.subscriptionOnBoard != 1) {
            subscribeAnnouncement(HomeActivity.getMainUser().getId(), info, (CheckBox) view);
        } else {
            unsubscribeAnnouncement(HomeActivity.getMainUser().getId(), info, (CheckBox) view);
        }
    }

    private void subscribeAnnouncement(int user_id, Board board, CheckBox favorite) {
        disposable.add(
                boardApi.subscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
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
                                Log.e("BoardsFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }

    private void unsubscribeAnnouncement(int user_id, Board board, CheckBox favorite) {
        disposable.add(
                boardApi.unsubscribeAnnouncement(String.valueOf(user_id), String.valueOf(board.id))
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
                                Log.e("BoardsFragment", "onError: " + e.getMessage());
                                Toast.makeText(getContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                                favorite.setChecked(!favorite.isChecked());
                                favorite.setEnabled(true);
                            }
                        })
        );
    }

    @Override
    public boolean onBackPressed() {
        return ((HomeActivity) getActivity()).backFragment();
    }
}
