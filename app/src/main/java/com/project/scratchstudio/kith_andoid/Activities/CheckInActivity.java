package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PhotoService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckInActivity extends AppCompatActivity {

    private PhotoService photoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        photoService = new PhotoService(this);
//
//        ImageView image = findViewById(R.id.portfolio);
//        image.setImageBitmap(photoService.decodingPhoto(getResources(), getResources().getIdentifier("person", "mipmap", getPackageName()), 210, 210));

        editTextListener(R.id.editText5);
        editTextListener(R.id.editText);
        editTextListener(R.id.editText4);
        editTextListener(R.id.editText6);
        editTextListener(R.id.editText7);
        editTextListener(R.id.editText8);
        editTextListener(R.id.editText9);

        CustomFontTextView t2 = findViewById(R.id.agree);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
        t2.setOnClickListener(view -> {
            Intent intent = new Intent(CheckInActivity.this, AgreementActivity.class);
            startActivityForResult(intent, 1);
        });

    }

    private void editTextListener(int id){
        EditText editText = findViewById(id);
        editText.setOnFocusChangeListener((view, b) -> {
            if(b)
                view.setBackgroundResource( R.drawable.entri_field_focus_check_in);
            else
                view.setBackgroundResource( R.drawable.entry_field_check_in);
        });
    }

    public void onClickCheckInButton(View view) {
        EditText editText = findViewById(R.id.editText5);
        if(editText.getText().toString().equals("")){
            ScrollView scrollView = findViewById(R.id.scroll);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            editText.setBackgroundResource(R.drawable.entri_field_error_check_in);
        }
        else{
            Intent intent = new Intent(CheckInActivity.this, SignInActivity.class);
            startActivity(intent);
        }

    }

    public void chooseImageButton(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
       CircleImageView image = findViewById(R.id.portfolio);
       if(requestCode == 0 && resultCode == RESULT_OK && intent != null && intent.getData() != null){
           final Uri imageUri = intent.getData();
           final InputStream imageStream;
           try {
               imageStream = getContentResolver().openInputStream(imageUri);
               Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//               selectedImage = photoService.decodingPhoto(selectedImage, imageUri);
               image.setImageBitmap(selectedImage);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
        }

    }

}
