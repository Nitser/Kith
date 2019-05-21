package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static long buttonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        User user = (User)bundle.getSerializable("user");

        if(user.getId() != HomeActivity.getMainUser().getId()){

        }

        if(isNetworkConnected()){

            ImageView photo = findViewById(R.id.photo);
            CustomFontTextView name = findViewById(R.id.name);
            CustomFontTextView surname = findViewById(R.id.surname);
            CustomFontTextView middlename = findViewById(R.id.middlename);
            CustomFontTextView position = findViewById(R.id.position);
            CustomFontTextView usersCount = findViewById(R.id.users_count);
            CustomFontTextView description = findViewById(R.id.description);

            Picasso.with(this).load(user.getUrl().replaceAll("@[0-9]*", ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .into(photo);
            name.setText(user.getFirstName());
            surname.setText(user.getLastName());
            middlename.setText(user.getMiddleName());
            position.setText(user.getPosition());
            usersCount.setText(String.valueOf(user.getUsersCount()));
            if(user.getDescription().equals("null") || user.getDescription().equals("")){
                CustomFontTextView label_description = findViewById(R.id.label_description);
                label_description.setVisibility(View.INVISIBLE);
            } else{
                description.setText(user.getDescription());
            }
        }
    }

    public void onClickExitButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        HomeActivity.cleanMainUser();
        InternalStorageService internalStorageService = new InternalStorageService(this);
        internalStorageService.setiSetInternalData(new ClearUserIdAndToken());
        internalStorageService.execute();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        view.setEnabled(true);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickEditButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        Intent intent = new Intent(ProfileActivity.this, EditActivity.class);
        startActivity(intent);
        view.setEnabled(true);
    }
}
