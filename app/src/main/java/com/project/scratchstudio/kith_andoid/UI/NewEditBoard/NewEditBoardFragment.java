package com.project.scratchstudio.kith_andoid.UI.NewEditBoard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PhotoService;
import com.project.scratchstudio.kith_andoid.app.BaseFragment;
import com.project.scratchstudio.kith_andoid.databinding.FragmentNewAnnouncementBinding;
import com.project.scratchstudio.kith_andoid.network.model.board.Board;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import static android.app.Activity.RESULT_OK;

public class NewEditBoardFragment extends BaseFragment {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static long buttonCount = 0;
    private FragmentNewAnnouncementBinding binding;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static NewEditBoardFragment newInstance(Bundle bundle) {
        NewEditBoardFragment fragment = new NewEditBoardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Bundle bundle;
    private Bitmap photo;
    private Board board;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_announcement, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        verifyStoragePermissions(getActivity());
        setButtonsListener();

        if (bundle.containsKey("is_edit") && bundle.getBoolean("is_edit")) {
            board = (Board) bundle.getSerializable("board");
            fillFields();
        }
    }

    private void fillFields() {
        binding.titleText.setText(board.title);
        binding.changeDescription.setText(board.description);
        if (board.url != null && !board.url.equals("null") && !board.url.equals("")) {
            Picasso.with(getActivity()).load(board.url.replaceAll("@[0-9]*", ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding.newPhoto);
            binding.newPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private void setButtonsListener() {
        binding.cancel.setOnClickListener(this::onClickClose);
        binding.loadPhoto.setOnClickListener(this::onClickLoadPhoto);
        binding.done.setOnClickListener(this::onClickDone);
    }

    @Override
    public boolean onBackPressed() {
        if (bundle.containsKey("is_edit") && bundle.getBoolean("is_edit")) {
            editBoard();
        }
        hideKeyboard(getActivity());
        return super.onBackPressed();
    }

    public void onClickDoneClose() {
        ((HomeActivity) getActivity()).changedBoardPhoto();

        onClickClose(null);
    }

    private void onClickDone(View view) {
        view.setEnabled(false);
        hideKeyboard(getActivity());
        if (binding.titleText.getText() == null || binding.titleText.getText().toString().equals("") || binding.changeDescription.getText() == null
                || binding.changeDescription.getText().toString()
                .equals("")) {
            Toast.makeText(getContext(), "Не заполнены все поля обьявления", Toast.LENGTH_SHORT).show();
        } else {
            if (isNetworkConnected()) {
                Board info = createBoard();
                HttpService httpService = new HttpService();

                if (board == null) {
                    httpService.addAnnouncement(getActivity(), HomeActivity.getMainUser(), this, info, photo);
                } else {
                    httpService.editAnnouncement(getActivity(), HomeActivity.getMainUser(), this, board, info, photo);
                }
            } else {
                Toast.makeText(getContext(), "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            }
        }
        view.setEnabled(true);
    }

    public void onClickClose(View view) {
        if (bundle.containsKey("is_edit") && bundle.getBoolean("is_edit")) {
            editBoard();
        }
        hideKeyboard(getActivity());
        ((HomeActivity) getActivity()).backFragment();
    }

    private void editBoard() {
        board.title = binding.titleText.getText().toString();
        board.description = binding.changeDescription.getText().toString();
    }

    private Board createBoard() {
        Board info = new Board();
        info.title = binding.titleText.getText().toString();
        info.description = binding.changeDescription.getText().toString();
        info.organizerId = HomeActivity.getMainUser().getId();

        return info;
    }

    private void onClickLoadPhoto(View view) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
        } else {
            startGallery(view);
        }
    }

    private void startGallery(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return;
        }

        view.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Button load = getActivity().findViewById(R.id.loadPhoto);
        if (requestCode == 0 && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
            Uri imageUri = intent.getData();
            InputStream imageStream;
            PhotoService photoService = new PhotoService(getActivity());
            try {
                imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                photo = BitmapFactory.decodeStream(imageStream);
                photo = photoService.changePhoto(photo, imageUri);
                photo = photoService.resizeBitmap(photo, binding.newPhoto.getWidth(), binding.newPhoto.getHeight());
                photo = photoService.compressPhoto(photo);
                binding.newPhoto.setImageBitmap(photo);
                binding.newPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            load.setEnabled(true);
        } else if (requestCode == 0) {
            load.setEnabled(true);
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null);
    }
}
