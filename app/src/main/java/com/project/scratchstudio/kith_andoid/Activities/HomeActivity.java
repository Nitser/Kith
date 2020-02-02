package com.project.scratchstudio.kith_andoid.Activities;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.scratchstudio.kith_andoid.Fragments.TreeFragment;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;
import com.project.scratchstudio.kith_andoid.SetInternalData.SetUserIdAndToken;
import com.project.scratchstudio.kith_andoid.UI.BoardInfo.BoardInfoFragment;
import com.project.scratchstudio.kith_andoid.UI.BoardList.BoardsFragment;
import com.project.scratchstudio.kith_andoid.UI.NewComment.NewCommentFragment;
import com.project.scratchstudio.kith_andoid.UI.NewEditBoard.NewEditBoardFragment;
import com.project.scratchstudio.kith_andoid.network.model.user.User;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {

    private Bundle bundle;
    private FragmentTransaction fragmentTransaction;
    private static User mainUser;
    private static List<User> invitedUsers = new ArrayList<>();
    private static List<Bundle> stackBundles = new ArrayList<>();
    private static List<AnnouncementInfo> boardsList = new ArrayList<>();

    private BottomNavigationView navigationView;

    public static User getMainUser() {
        return mainUser;
    }

    public static List<Bundle> getStackBundles() {
        return stackBundles;
    }

    public static List<AnnouncementInfo> getBoardsList() {
        return boardsList;
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
                replaceFragment(TreeFragment.newInstance(bundle));
                return true;
            case R.id.navigation_announcements:
                replaceFragment(BoardsFragment.newInstance(bundle));
                return true;
        }
        return false;
    };

    public void addFragment(Fragment fragment, String tag) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(R.id.container, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public void backFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void setTreeNavigation() {
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void replaceFragment(Fragment fragment) {
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
        replaceFragment(TreeFragment.newInstance(bundle));

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for (Fragment f : fragmentList) {
            if (f instanceof TreeFragment) {
                handled = ((TreeFragment) f).onBackPressed();
                if (handled) {
                    break;
                }
            } else if (f instanceof NewEditBoardFragment) {
                handled = ((NewEditBoardFragment) f).onBackPressed();
                if (handled) {
                    break;
                }
            } else if (f instanceof BoardInfoFragment) {
                handled = ((BoardInfoFragment) f).onBackPressed();
                if (handled) {
                    break;
                }
            } else if (f instanceof BoardsFragment) {
                handled = ((BoardsFragment) f).onBackPressed();
                if (handled) {
                    break;
                }
            } else if (f instanceof NewCommentFragment) {
                handled = ((NewCommentFragment) f).onBackPressed();
                if (handled) {
                    break;
                }
            }
        }
        if (!handled) {
            super.onBackPressed();
        }
    }

}
