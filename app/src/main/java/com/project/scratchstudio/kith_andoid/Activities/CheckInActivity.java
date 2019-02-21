package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
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

        CustomFontTextView agreement = findViewById(R.id.agree);
        customTextView(agreement);

    }

    private void customTextView(CustomFontTextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "Я согласен с ");
        spanTxt.append("пользовательским соглашением");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(CheckInActivity.this, AgreementActivity.class);
                startActivityForResult(intent, 1);
            }
        }, spanTxt.length() - "пользовательским соглашением".length(), spanTxt.length(), 0);
        spanTxt.append(" и ");
//        spanTxt.setSpan(new ForegroundColorSpan(Color.WHITE), 32, spanTxt.length(), 0);
        spanTxt.append("политикой конфиденциальности");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(CheckInActivity.this, TermActivity.class);
                startActivityForResult(intent, 2);
            }
        }, spanTxt.length() - "политикой конфиденциальности".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
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
            finish();
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
