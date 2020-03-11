package com.project.scratchstudio.kith_andoid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.scratchstudio.kith_andoid.Fragments.TreeFragment;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken;
import com.project.scratchstudio.kith_andoid.SetInternalData.SetUserIdAndToken;
import com.project.scratchstudio.kith_andoid.UI.BoardInfo.BoardInfoFragment;
import com.project.scratchstudio.kith_andoid.UI.BoardList.BoardsFragment;
import com.project.scratchstudio.kith_andoid.UI.Comments.CommentListFragment;
import com.project.scratchstudio.kith_andoid.app.FragmentType;
import com.project.scratchstudio.kith_andoid.network.model.user.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {

    private Bundle bundle;
    private FragmentTransaction fragmentTransaction;
    private BoardsFragment boardsFragment;
    private static User mainUser;
    private static List<User> invitedUsers = new ArrayList<>();
    private static List<Bundle> stackBundles = new ArrayList<>();
    private static Fragment active;

    private BottomNavigationView navigationView;

    public static User getMainUser() {
        return mainUser;
    }

    public static List<Bundle> getStackBundles() {
        return stackBundles;
    }

    public void setInvitedUsers(List<User> list) {
        invitedUsers = list;
    }

    public static void cleanMainUser() {
        mainUser = null;
    }

    public static void createMainUser() {
        mainUser = new User();
    }

    public static void createInvitedUsers() {
        invitedUsers = new ArrayList<>();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_tree:
                bundle = stackBundles.get(stackBundles.size() - 1);
                replaceFragment(TreeFragment.newInstance(bundle, FragmentType.TREE.name()), FragmentType.TREE.name());
                return true;
            case R.id.navigation_announcements:
                boardsFragment = BoardsFragment.newInstance(bundle, FragmentType.BOARD_LIST.name());
                replaceFragment(boardsFragment, FragmentType.BOARD_LIST.name());
                return true;
        }
        return false;
    };

    private BottomNavigationView.OnNavigationItemReselectedListener mOnNavigationItemReSelectedListener = menuItem -> {
//        fragmentTransaction.
        getSupportFragmentManager().popBackStackImmediate();
    };


    public void addFragment(Fragment fragment, String tag) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(R.id.container, fragment, tag);
//        if(active != null)
//            fragmentTransaction.hide(active);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void removeFragment(Fragment fragment, String tag) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.remove(fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public boolean backFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    public void setTreeNavigation() {
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InternalStorageService internalStorageService = new InternalStorageService(this);
        internalStorageService.setiSetInternalData(new SetUserIdAndToken());
        internalStorageService.execute();

        bundle = getIntent().getExtras();
        stackBundles.add(bundle);
        replaceFragment(TreeFragment.newInstance(bundle, FragmentType.TREE.name()), FragmentType.TREE.name());

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigationView.setOnNavigationItemReselectedListener(mOnNavigationItemReSelectedListener);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            backFragment();
        }
    }

    public void exit(){
        HomeActivity.cleanMainUser();
        InternalStorageService internalStorageService = new InternalStorageService(this);
        internalStorageService.setiSetInternalData(new ClearUserIdAndToken());
        internalStorageService.execute();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void changedBoardPhoto() {
//        BoardsFragment boardsFragment = (BoardsFragment) getSupportFragmentManager().findFragmentByTag(FragmentType.BOARD_LIST.name());
        BoardInfoFragment boardInfoFragment = (BoardInfoFragment) getSupportFragmentManager().findFragmentByTag(FragmentType.BOARD_INFO.name());
        if (boardInfoFragment != null) {
            Log.i("RELOAD", "BOARD_INFO NOT NULL");
            boardInfoFragment.reloadPhoto();
        } else {
            Log.i("RELOAD", "BOARD_INFO IS NULL");
        }
        if (boardsFragment != null) {
            Log.i("RELOAD", "BOARD_LIST NOT NULL");
            boardsFragment.reloadPhotoById();
        } else {
            Log.i("RELOAD", "BOARD_LIST IS NULL");
        }
    }

    public void updateComments() {
        CommentListFragment commentListFragment = (CommentListFragment) getSupportFragmentManager()
                .findFragmentByTag(FragmentType.COMMENT_LIST.name());
        if (commentListFragment != null) {
            commentListFragment.reloadComments();
        }
    }

    public void updateBoards() {
        if (boardsFragment != null) {
            boardsFragment.reloadBoards();
        }
    }

}
