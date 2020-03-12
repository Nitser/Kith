package com.project.scratchstudio.kith_andoid.Activities;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PhotoService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.UserPresenter;
import com.project.scratchstudio.kith_andoid.databinding.ActivityEditProfileBinding;
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class EditActivity extends AppCompatActivity {

    private static final int DATA_CHANGED = 0;
    private static final int DATA_NOT_CHANGED = 1;
    private static long buttonCount = 0;
    private boolean isChanged = false;
    private Bitmap currentBitmap;
    private List<EditText> fields = new ArrayList<>();
    private Context context;

    private UserPresenter userPresenter;
    ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        init();
        userPresenter = new UserPresenter(this);
        context = this;
    }

    private void init() {
        User mainUser = HomeActivity.getMainUser();

        fields.add(binding.firstName);
        fields.add(binding.lastName);
        fields.add(binding.middleName);
        fields.add(binding.position);
        fields.add(binding.email);
        fields.add(binding.phone);
        fields.add(binding.description);
        Picasso.with(this).load(mainUser.photo.replaceAll("@[0-9]*", ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(new PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(binding.portfolio);

        binding.firstName.setText(mainUser.getFirstName());
        binding.lastName.setText(mainUser.getLastName());
        if (mainUser.getMiddleName() != null && !mainUser.getMiddleName().toLowerCase().equals("null")) {
            binding.middleName.setText(mainUser.getMiddleName());
        }
        binding.password.setText(mainUser.getPosition());
        if (mainUser.getEmail() != null && !mainUser.getEmail().toLowerCase().equals("null")) {
            binding.email.setText(mainUser.getEmail());
        }
        binding.phone.setText(mainUser.getPhone());
        if (mainUser.getDescription() != null && !mainUser.getDescription().toLowerCase().equals("null")) {
            binding.description.setText(mainUser.getDescription());
        }
        binding.password.setText(new String(new char[mainUser.getPassword().length()]).replace("\0", "*"));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private User makeRefreshUser() {
        User user = new User();
        try {
            user.setId(HomeActivity.getMainUser().getId());
            user.setToken(HomeActivity.getMainUser().getToken());
            user.setFirstName(Objects.requireNonNull(fields.get(0).getText()).toString());
            user.setLastName(Objects.requireNonNull(fields.get(1).getText()).toString());
            user.setMiddleName(Objects.requireNonNull(fields.get(2).getText()).toString().replaceAll("^null$", ""));
            user.setPosition(Objects.requireNonNull(fields.get(3).getText()).toString());
            user.setEmail(Objects.requireNonNull(fields.get(4).getText()).toString().replaceAll("^null$", ""));
            user.setPhone(Objects.requireNonNull(fields.get(5).getText()).toString());
            user.setDescription(Objects.requireNonNull(fields.get(6).getText()).toString().replaceAll("^null$", ""));

        } catch (NullPointerException ignored) {
        }

        return user;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0 && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
            Uri imageUri = intent.getData();
            InputStream imageStream;
            PhotoService photoService = new PhotoService(this);
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                currentBitmap = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            currentBitmap = photoService.changePhoto(currentBitmap, imageUri);
            currentBitmap = photoService.changePhoto(currentBitmap, imageUri);
            currentBitmap = photoService.compressPhoto(currentBitmap);
            binding.portfolio.setImageBitmap(currentBitmap);
            binding.buttonPhoto.setEnabled(true);
        } else if (requestCode == 0) {
            binding.buttonPhoto.setEnabled(true);
        } else if (requestCode == 3 && resultCode == DATA_CHANGED && intent != null) {
            binding.password.setText(new String(new char[HomeActivity.getMainUser().getPassword().length()]).replace("\0", "*"));
        }
    }

    public void chooseImageButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return;
        }
        view.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    public void onClickCancelButton(View view) {
        finish();
    }

    public void onClickRefreshButton(View view) {
        view.setEnabled(false);
        User refreshUser = makeRefreshUser();
        userPresenter.editUser(new UserPresenter.EditUserCallback() {
            @Override
            public void onSuccess(final BaseResponse baseResponse) {
                if (baseResponse.getStatus()) {
                    isChanged = true;
                    finishEdit();
                } else {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                }
            }

            @Override
            public void onError(final NetworkErrorException networkError) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show();
                view.setEnabled(true);
            }
        }, refreshUser, currentBitmap);
    }

    public void finishEdit() {
        Intent intent = new Intent();
        if (isChanged) {
            setResult(DATA_CHANGED, intent);
        } else {
            setResult(DATA_NOT_CHANGED, intent);
        }
        finish();
    }

    public void onClickChangePassword(View view) {
        view.setEnabled(false);
        Intent intent = new Intent(EditActivity.this, ChangePasswordActivity.class);
        startActivityForResult(intent, 3);
        view.setEnabled(true);
    }
}
