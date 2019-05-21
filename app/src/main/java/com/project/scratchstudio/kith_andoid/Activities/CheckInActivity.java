package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.Cache;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.PhotoService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckInActivity extends AppCompatActivity {

    private long buttonCount = 0;
    private static boolean status = true;
    private HttpService httpService = new HttpService();
    private Map<String, CustomFontEditText> requiredFields = new HashMap<>();

    private ImageButton photoButton;
    private Button checkInButton;
    private Bitmap currentBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        editTextInitialized();

        CustomFontTextView agreement = findViewById(R.id.agree);
        customTextView(agreement);

        photoButton = findViewById(R.id.buttonPhoto);
        Button button = findViewById(R.id.button3);
        button.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/intro_regular.ttf"));

    }

    private void customTextView(CustomFontTextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "Я согласен с ");
        spanTxt.append("пользовательским соглашением");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (SystemClock.elapsedRealtime() - buttonCount < 1000)
                    return;
                buttonCount = SystemClock.elapsedRealtime();

                Intent intent = new Intent(CheckInActivity.this, AgreementActivity.class);
                startActivityForResult(intent, 1);
            }
        }, spanTxt.length() - "пользовательским соглашением".length(), spanTxt.length(), 0);
        spanTxt.append(" и ");
        spanTxt.append("политикой конфиденциальности");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (SystemClock.elapsedRealtime() - buttonCount < 1000)
                    return;
                buttonCount = SystemClock.elapsedRealtime();
                Intent intent = new Intent(CheckInActivity.this, TermActivity.class);
                startActivityForResult(intent, 2);
            }
        }, spanTxt.length() - "политикой конфиденциальности".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void editTextInitialized(){
        editTextListener("user_lastname", R.id.editText); // F
        editTextListener("user_firstname", R.id.editText5);// N
        requiredFields.put("user_middlename",(CustomFontEditText) findViewById(R.id.editText6));// O
        editTextListener("user_login", R.id.editText7);// Login
        editTextListener("user_password", R.id.editText8);// Password
        editTextListener("user_phone", R.id.editText4);// Phone
        editTextListener("user_position", R.id.editText1);// Position
        editTextListener("ref_code", R.id.editText9);// Code
        requiredFields.put("user_description", (CustomFontEditText) findViewById(R.id.editText2));// Description

//        CustomFontEditText phone = findViewById(R.id.editText4);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("RU"));
//        }
    }

    private void editTextListener(String key, int id){
        CustomFontEditText editText = findViewById(id);
        editText.setOnFocusChangeListener((view, b) -> {
            if(b)
                view.setBackgroundResource( R.drawable.entri_field_focus_check_in);
            else
                view.setBackgroundResource( R.drawable.entry_field_check_in);
        });
        requiredFields.put(key, editText);
    }

    public void onClickCheckInButton(View view) {
        checkInButton = (Button) view;
        view.setEnabled(false);
        status = true;
        CustomFontEditText referralCode = findViewById(R.id.editText9);
        if(!isNetworkConnected() ){
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            view.setEnabled(true);
        } else {
            httpService.checkReferral(this, String.valueOf(referralCode.getText()), view);
        }

    }

    public void checkFields(){
        Pattern p = Pattern.compile("^(\\+7|8){1}\\s?(\\d){3}\\s?(\\d){3}\\-?(\\d){2}\\-?(\\d){2}");

        for(Map.Entry<String, CustomFontEditText> field : requiredFields.entrySet()){
            if(!field.getKey().equals("user_middlename") && !field.getKey().equals( "user_description")){
                Matcher m = p.matcher(field.getValue().getText());
                if(field.getValue().getText().toString().trim().equals("")){
                    field.getValue().setBackgroundResource(R.drawable.entri_field_error_check_in);
                    status = false;
                }
                if(field.getKey().equals("user_password") && field.getValue().length()<6){
                    field.getValue().setBackgroundResource(R.drawable.entri_field_error_check_in);
                    status = false;
                    Toast.makeText(this, "Пароль должен быть не короче 6 символов ", Toast.LENGTH_LONG).show();
                }
                if(field.getKey().equals("user_phone") && !m.find()){
//                    Log.i("PHONE: ", field.getValue().getText().toString());
                    field.getValue().setBackgroundResource(R.drawable.entri_field_error_check_in);
                    status = false;
                    Toast.makeText(this, "Неверный формат телефона", Toast.LENGTH_LONG).show();
                }
            }
        }
        CheckBox check = findViewById(R.id.checkbox);
        if(!check.isChecked()){
          status = false;
          check.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
          checkInButton.setEnabled(true);
        }
    }

    public void checkIn(String parent_id) throws UnsupportedEncodingException {
        if(status){
            httpService.singup(this, checkInButton, currentBitmap, parent_id, requiredFields );
        } else {
            ScrollView scrollView = findViewById(R.id.scroll);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }
        checkInButton.setEnabled(true);
    }

    public void chooseImageButton(View view) {
        view.setEnabled(false);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
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
           image.setImageBitmap(currentBitmap);
           photoButton.setEnabled(true);
        }
        else if( requestCode == 0 ){
           photoButton.setEnabled(true);
       }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void onCheckButton(View view) {
        CheckBox check = findViewById(R.id.checkbox);
        check.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDark)));
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
