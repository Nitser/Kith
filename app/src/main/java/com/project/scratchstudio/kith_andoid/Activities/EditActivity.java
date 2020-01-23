package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PhotoService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private static final int DATA_CHANGED = 0;
    private static final int DATA_NOT_CHANGED = 1;
    private static long buttonCount = 0;
    private boolean isChanged = false;
    private Bitmap currentBitmap;
    private ImageButton photoButton;
    private List<CustomFontEditText> fields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }

    private void init(){
        User mainUser = HomeActivity.getMainUser();
        ImageView photo = findViewById(R.id.portfolio);
        photoButton = findViewById(R.id.buttonPhoto);
        CustomFontTextView password = findViewById(R.id.password);
        CustomFontEditText firstName = findViewById(R.id.firstName);
        CustomFontEditText lastName = findViewById(R.id.lastName);
        CustomFontEditText middleName = findViewById(R.id.middleName);
        CustomFontEditText position = findViewById(R.id.position);
        CustomFontEditText email = findViewById(R.id.email);
        CustomFontEditText phone = findViewById(R.id.phone);
        CustomFontEditText description = findViewById(R.id.description);
        fields.add(firstName); fields.add(lastName); fields.add(middleName); fields.add(position); fields.add(email); fields.add(phone); fields.add(description);
        Picasso.with(this).load(mainUser.getUrl().replaceAll("@[0-9]*", ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(new PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(photo);

        firstName.setText(mainUser.getFirstName());
        lastName.setText(mainUser.getLastName());
        if(mainUser.getMiddleName() != null && !mainUser.getMiddleName().toLowerCase().equals("null"))
            middleName.setText(mainUser.getMiddleName());
        position.setText(mainUser.getPosition());
        if(mainUser.getEmail() != null && !mainUser.getEmail().toLowerCase().equals("null"))
            email.setText(mainUser.getEmail());
        phone.setText(mainUser.getPhone());
        if(mainUser.getDescription() != null && !mainUser.getDescription().toLowerCase().equals("null"))
            description.setText(mainUser.getDescription());
        password.setText(new String(new char[mainUser.getPassword().length()]).replace("\0", "*"));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private User makeRefreshUser(){
        User user = new User();
        try{
            user.setId(HomeActivity.getMainUser().getId());
            user.setToken(HomeActivity.getMainUser().getToken());
            user.setFirstName(Objects.requireNonNull(fields.get(0).getText()).toString());
            user.setLastName(Objects.requireNonNull(fields.get(1).getText()).toString());
            user.setMiddleName(Objects.requireNonNull(fields.get(2).getText()).toString().replaceAll("^null$", ""));
            user.setPosition(Objects.requireNonNull(fields.get(3).getText()).toString());
            user.setEmail(Objects.requireNonNull(fields.get(4).getText()).toString().replaceAll("^null$", ""));
            user.setPhone(Objects.requireNonNull(fields.get(5).getText()).toString());
            user.setDescription(Objects.requireNonNull(fields.get(6).getText()).toString().replaceAll("^null$", ""));

//            user.setUrl();
        } catch (NullPointerException ignored){ }

        return user;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 0 && resultCode == RESULT_OK && intent != null && intent.getData() != null){
            CircleImageView image = findViewById(R.id.portfolio);
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
            currentBitmap = photoService.compressPhoto(currentBitmap, "");
            image.setImageBitmap(currentBitmap);
//            HomeActivity.getMainUser().setUrl();
            photoButton.setEnabled(true);
            isChanged = true;
        }
        else if( requestCode == 0 ) {
            photoButton.setEnabled(true);
        }
        else if(requestCode == 3 && resultCode == DATA_CHANGED && intent != null) {
            CustomFontTextView password = findViewById(R.id.password);
            password.setText(new String(new char[HomeActivity.getMainUser().getPassword().length()]).replace("\0", "*"));
        }
    }

    public void chooseImageButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        view.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    public void onClickCancelButton(View view) {
        Intent intent = new Intent(EditActivity.this, ProfileActivity.class);
//        if(isChanged)
//            setResult(PASSWORD_CHANGED, intent);
//        else
        setResult(DATA_NOT_CHANGED, intent);
        finish();
    }

    public void onClickRefreshButton(View view) throws UnsupportedEncodingException {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        view.setEnabled(false);
        HttpService service = new HttpService();
        if(!isNetworkConnected()){
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
        } else {
            User refreshUser = makeRefreshUser();
            service.refreshUser(this, refreshUser, currentBitmap, true);
            isChanged = true;
        }
    }

    public void finishEdit(){
        Intent intent = new Intent(EditActivity.this, ProfileActivity.class);
        if(isChanged)
            setResult(DATA_CHANGED, intent);
        else
            setResult(DATA_NOT_CHANGED, intent);
        finish();
    }

    public void onClickChangePassword(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        Intent intent = new Intent(EditActivity.this, ChangePasswordActivity.class);
        startActivityForResult(intent, 3);
    }
}
