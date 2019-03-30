package com.project.scratchstudio.kith_andoid.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.HttpService;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken;
import com.project.scratchstudio.kith_andoid.SetInternalData.SetUserIdAndToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static User mainUser;
    private  Bundle bundle;
    private HttpService httpService;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private  List<User> invitedUsers = new ArrayList<>();

    public static User getMainUser() {
        return mainUser;
    }
    public void setMainUser(List <User> list) { invitedUsers = list; }
    public List<User> getInvitedUsers() {
        return invitedUsers;
    }
    public static void cleanMainUser() {
        mainUser = null;
    }
    public static void createMainUser(){ mainUser = new User(); }
    public void createInvitedUsers() { invitedUsers = new ArrayList<>(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InternalStorageService internalStorageService = new InternalStorageService(this);
        internalStorageService.setiSetInternalData(new SetUserIdAndToken());
        internalStorageService.execute();

        httpService = new HttpService();

       bundle = getIntent().getExtras();
        if(bundle!= null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")){
            if(mainUser.getFirstName() == null){
                //get all data about user
                httpService.getUser(this);
            } else {
                CustomFontTextView name = findViewById(R.id.name);
                String userName = mainUser.getFirstName() + " " + mainUser.getLastName();
                name.setText(userName);
            }

            httpService.referralCount(this, false, mainUser.getId());
            httpService.referralCount(this, true, mainUser.getId());

            createInvitedUsers();
            httpService.getInvitedUsers(this, mainUser.getId(), true);
        } else if(bundle != null && bundle.containsKey("user")){
            User user = (User)bundle.getSerializable("user");
            CustomFontTextView name = findViewById(R.id.name);

            ImageButton back = findViewById(R.id.back);
            back.setVisibility(View.VISIBLE);

            ImageView photo = findViewById(R.id.photo);
            if(user.getUrl() != null)
            Picasso.with(this).load(user.getUrl().replaceAll("@[0-9]*", ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .into(photo);

            httpService.referralCount(this, true, user.getId());

            String userName = user.getFirstName() + " " + user.getLastName();
            name.setText(userName);

            httpService.referralCount(this, false, user.getId());
            httpService.getInvitedUsers(this, user.getId(), false);

        }

        mySwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this::myUpdateOperation
        );

    }

    private void myUpdateOperation(){
//        Log.i("REFRESH: ", "onRefresh called from SwipeRefreshLayout");
        Activity activity = this;

        if(isNetworkConnected()){
            new Handler().post(() -> {
                if( bundle!= null && bundle.containsKey("another_user") && !bundle.getBoolean("another_user")){
                    httpService.referralCount(activity, false, mainUser.getId());
                    httpService.referralCount(activity, true, mainUser.getId());

                    httpService.refreshInvitedUsers(activity, mainUser.getId(), true, mySwipeRefreshLayout);
                } else {
                    User user = (User)bundle.getSerializable("user");
                    httpService.referralCount(this, false, user.getId());
                    httpService.refreshInvitedUsers(activity, user.getId(), false, mySwipeRefreshLayout);
                }

            });
        } else {
            Toast.makeText(activity, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            mySwipeRefreshLayout.setRefreshing(false);
        }


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) ;
    }

    public void onClickBackButton(View view) {
        cleanMainUser();
        InternalStorageService internalStorageService = new InternalStorageService(this);
        internalStorageService.setiSetInternalData(new ClearUserIdAndToken());
        internalStorageService.execute();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickBack(View view) {
        finish();
    }
}
