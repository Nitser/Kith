package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.network.model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static long buttonCount = 0;
    private User user;
    private TextView share;

    private TextView name;
    private TextView surname;
    private TextView middlename;
    private TextView phone;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user");

        if (user.getId() != HomeActivity.getMainUser().getId()) {
            ImageButton edit = findViewById(R.id.edit);
            edit.setVisibility(View.INVISIBLE);
            TextView exit = findViewById(R.id.exit);
            exit.setVisibility(View.INVISIBLE);

            Button share = findViewById(R.id.my_share);
            share.setVisibility(View.GONE);
            ImageView share2 = findViewById(R.id.share);
            share2.setVisibility(View.VISIBLE);
        }

        if (isNetworkConnected()) {

            ImageView photo = findViewById(R.id.photo);
            name = findViewById(R.id.name);
            surname = findViewById(R.id.surname);
            middlename = findViewById(R.id.middlename);
            TextView position = findViewById(R.id.position);
            TextView usersCount = findViewById(R.id.users_count);
            TextView description = findViewById(R.id.description);
            email = findViewById(R.id.email);
            phone = findViewById(R.id.phone);

            Picasso.with(this).load(user.getUrl().replaceAll("@[0-9]*", ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(photo);
            name.setText(user.getFirstName());
            surname.setText(user.getLastName());
            phone.setText(user.getPhone());
            if (user.getMiddleName() != null && !user.getMiddleName().equals("") && !user.getMiddleName().toLowerCase().equals("null")) {
                middlename.setText(user.getMiddleName());
            } else {
                middlename.setText("-");
            }
            position.setText(user.getPosition());
            usersCount.setText(String.valueOf(user.getUsersCount()));
            if (user.getDescription() == null || user.getDescription().equals("null") || user.getDescription().equals("")) {
                description.setText("-");
            } else {
                description.setText(user.getDescription());
            }

            if (user.getEmail() == null || user.getEmail().equals("null") || user.getEmail().equals("")) {
                email.setText("-");
            } else {
                email.setText(user.getEmail());
            }
        }
    }

    public void onClickExitButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
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
        return (cm != null && cm.getActiveNetworkInfo() != null);
    }

    public void onClickBack(View view) {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            share.setEnabled(true);
        }
        if (requestCode == 1) {
            if (data != null && resultCode == 0) {
                if (isNetworkConnected()) {
                    refreshUser();
                } else {
                    Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void refreshUser() {
        ImageView photo = findViewById(R.id.photo);
        TextView name = findViewById(R.id.name);
        TextView surname = findViewById(R.id.surname);
        TextView middlename = findViewById(R.id.middlename);
        TextView position = findViewById(R.id.position);
        TextView usersCount = findViewById(R.id.users_count);
        TextView description = findViewById(R.id.description);
        TextView phone = findViewById(R.id.phone);
        TextView email = findViewById(R.id.email);

        User user = HomeActivity.getMainUser();
        Picasso.with(this).load(user.getUrl().replaceAll("@[0-9]*", ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(new PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(photo);
        name.setText(user.getFirstName());
        surname.setText(user.getLastName());
        phone.setText(user.getPhone());
        if (user.getMiddleName() != null && !user.getMiddleName().equals("") && !user.getMiddleName().toLowerCase().equals("null")) {
            middlename.setText(user.getMiddleName());
        } else {
            middlename.setText("-");
        }
        position.setText(user.getPosition());
        usersCount.setText(String.valueOf(user.getUsersCount()));
        if (user.getDescription() == null || user.getDescription().equals("") || user.getDescription().toLowerCase().equals("null")) {
            description.setText("-");
        } else {
            description.setText(user.getDescription());
        }
        if (user.getEmail() == null || user.getEmail().equals("null") || user.getEmail().equals("")) {
//                TextView label_email = findViewById(R.id.label_email);
//                ((ViewGroup)label_email.getParent()).removeView(label_email);
//                ((ViewGroup)email.getParent()).removeView(email);
            email.setText("-");
        } else {
            email.setText(user.getEmail());
        }
    }

    public void onClickEditButton(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);
        Intent intent = new Intent(ProfileActivity.this, EditActivity.class);
        startActivityForResult(intent, 1);
        view.setEnabled(true);
    }

    public void onClickCallPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + user.getPhone()));
        startActivity(intent);
    }

    public void onClickContactShare(View view) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return;
        }
        buttonCount = SystemClock.elapsedRealtime();
        view.setEnabled(false);

        String result = surname.getText() + " " + name.getText() + " " + middlename.getText() + "\n" + phone.getText() + "\n" + email.getText() + "\n"
                + getString(R.string.signature);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, result);
        sendIntent.setType("text/plain");
        startActivityForResult(sendIntent, 0);
    }

}
