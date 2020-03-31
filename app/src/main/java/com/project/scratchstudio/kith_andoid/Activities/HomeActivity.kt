package com.project.scratchstudio.kith_andoid.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.ui.home_package.BoardInfo.BoardInfoFragment
import com.project.scratchstudio.kith_andoid.ui.home_package.BoardList.BoardsFragment
import com.project.scratchstudio.kith_andoid.ui.home_package.Comments.CommentListFragment
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.ui.entry_package.main.MainActivity

import java.util.ArrayList

class HomeActivity : BaseActivity() {

    private lateinit var boardsFragment: BoardsFragment
    private lateinit var navigationView: BottomNavigationView

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{ item ->
        when (item.getItemId()) {
            R.id.navigation_tree -> {
                bundle = stackBundles[stackBundles.size - 1]
//                replaceFragment(TreeFragment.newInstance(bundle), FragmentType.TREE.name)
            }
            R.id.navigation_announcements -> {
//                boardsFragment = BoardsFragment.newInstance(bundle, FragmentType.BOARD_LIST.name)
                replaceFragment(boardsFragment, FragmentType.BOARD_LIST.name)
            }
        }
        false
    }

    private val mOnNavigationItemReSelectedListener = BottomNavigationView.OnNavigationItemReselectedListener{
        //        fragmentTransaction.
        supportFragmentManager.popBackStackImmediate()
    }

    fun setInvitedUsers(list: List<User>) {
        invitedUsers = list
    }


    fun setTreeNavigation() {
        navigationView.menu.getItem(0).isChecked = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        val internalStorageService = InternalStorageService(this)
//        internalStorageService.setISetInternalData(SetUserIdAndToken())
//        internalStorageService.execute()

        bundle = if (intent.extras != null) intent.extras else Bundle()
//        stackBundles.add(bundle)
//        replaceFragment(TreeFragment.newInstance(bundle), FragmentType.TREE.name)

        navigationView = findViewById(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigationView.setOnNavigationItemReselectedListener(mOnNavigationItemReSelectedListener)
    }

    fun exit() {
        cleanMainUser()
//        val internalStorageService = InternalStorageService(this)
//        internalStorageService.setISetInternalData(ClearUserIdAndToken())
//        internalStorageService.execute()
        val intent = Intent(this@HomeActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun changedBoardPhoto() {
        //        BoardsFragment boardsFragment = (BoardsFragment) getSupportFragmentManager().findFragmentByTag(FragmentType.BOARD_LIST.name());
        val boardInfoFragment = supportFragmentManager.findFragmentByTag(FragmentType.BOARD_INFO.name) as BoardInfoFragment?
        if (boardInfoFragment != null) {
            Log.i("RELOAD", "BOARD_INFO NOT NULL")
            boardInfoFragment.reloadPhoto()
        } else {
            Log.i("RELOAD", "BOARD_INFO IS NULL")
        }
        Log.i("RELOAD", "BOARD_LIST NOT NULL")
        boardsFragment.reloadPhotoById()
    }

    fun updateComments() {
        val commentListFragment = supportFragmentManager
                .findFragmentByTag(FragmentType.COMMENT_LIST.name) as CommentListFragment?
        commentListFragment?.reloadComments()
    }

    fun updateBoards() {
        boardsFragment.reloadBoards()
    }

    companion object {
        lateinit var mainUser: User
        private var invitedUsers: List<User> = ArrayList()


        fun cleanMainUser() {
            mainUser = User()
        }

        fun createMainUser() {
            mainUser = User()
        }

        fun createInvitedUsers() {
            invitedUsers = ArrayList()
        }
    }

}
