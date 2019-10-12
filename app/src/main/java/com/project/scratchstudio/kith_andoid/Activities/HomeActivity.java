package com.project.scratchstudio.kith_andoid.Activities;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.project.scratchstudio.kith_andoid.Fragments.AnnouncementFragment;
import com.project.scratchstudio.kith_andoid.Fragments.AnnouncementInfoFragment;
import com.project.scratchstudio.kith_andoid.Fragments.DialogFragment;
import com.project.scratchstudio.kith_andoid.Fragments.MessagesFragment;
import com.project.scratchstudio.kith_andoid.Fragments.NewAnnouncementFragment;
import com.project.scratchstudio.kith_andoid.Fragments.NewCommentFragment;
import com.project.scratchstudio.kith_andoid.Fragments.SearchFragment;
import com.project.scratchstudio.kith_andoid.Fragments.TreeFragment;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.Model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService;
import com.project.scratchstudio.kith_andoid.SetInternalData.SetUserIdAndToken;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Bundle bundle;
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

    public static List<AnnouncementInfo> getBoardsList() { return  boardsList; }
    public static void setBoardsList( List<AnnouncementInfo> list ) { boardsList = list; }

    public void setInvitedUsers(List <User> list) { invitedUsers = list; }
    public static void cleanMainUser() {
        mainUser = null;
    }
    public static void createMainUser(){ mainUser = new User(); }
    public static void createInvitedUsers() { invitedUsers = new ArrayList<>(); }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_tree:
                        bundle = stackBundles.get(stackBundles.size()-1);
                        loadFragment(TreeFragment.newInstance(bundle));
                        return true;
                    case R.id.navigation_announcements:
                        loadFragment(AnnouncementFragment.newInstance(bundle));
                        return true;
//                    case R.id.navigation_messages:
//                        loadFragment(MessagesFragment.newInstance(bundle));
//                        return true;
                }
                return false;
            };

    public void setTreeNavigation(){
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void loadFragment(Fragment fragment) {
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
        loadFragment(TreeFragment.newInstance(bundle));

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for(Fragment f : fragmentList) {
            if(f instanceof TreeFragment) {
                handled = ((TreeFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            }
//            else if(f instanceof SearchFragment){
//                handled = ((SearchFragment)f).onBackPressed();
//                if(handled) {
//                    break;
//                }
//            }
            else  if(f instanceof NewAnnouncementFragment){
                handled = ((NewAnnouncementFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            } else if(f instanceof MessagesFragment){
                handled = ((MessagesFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            } else if(f instanceof DialogFragment){
                handled = ((DialogFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            } else if(f instanceof AnnouncementInfoFragment){
                handled = ((AnnouncementInfoFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            } else if(f instanceof AnnouncementFragment){
                handled = ((AnnouncementFragment)f).onBackPressed();
                if(handled) {
                    break;
                }
            } else if(f instanceof NewCommentFragment){
                handled = ((NewCommentFragment)f).onBackPressed();
                if(handled)
                    break;
            }
        }
        if(!handled) {
            super.onBackPressed();
        }
    }

}
