package com.project.scratchstudio.kith_andoid.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.scratchstudio.kith_andoid.Fragments.TreeFragment
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService
import com.project.scratchstudio.kith_andoid.SetInternalData.ClearUserIdAndToken
import com.project.scratchstudio.kith_andoid.SetInternalData.SetUserIdAndToken
import com.project.scratchstudio.kith_andoid.ui.BoardInfo.BoardInfoFragment
import com.project.scratchstudio.kith_andoid.ui.BoardList.BoardsFragment
import com.project.scratchstudio.kith_andoid.ui.Comments.CommentListFragment
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.network.model.user.User

import java.util.ArrayList

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class HomeActivity : AppCompatActivity() {

    private lateinit var bundle: Bundle
    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var boardsFragment: BoardsFragment
    private lateinit var navigationView: BottomNavigationView

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{ item ->
        when (item.getItemId()) {
            R.id.navigation_tree -> {
                bundle = stackBundles[stackBundles.size - 1]
                replaceFragment(TreeFragment.newInstance(bundle), FragmentType.TREE.name)
            }
            R.id.navigation_announcements -> {
                boardsFragment = BoardsFragment.newInstance(bundle, FragmentType.BOARD_LIST.name)
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

    fun addFragment(fragment: Fragment, tag: String) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.add(R.id.container, fragment, tag)
        //        if(active != null)
        //            fragmentTransaction.hide(active);
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    fun removeFragment(fragment: Fragment, tag: String) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.remove(fragment)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    fun backFragment(): Boolean {
        if (supportFragmentManager.backStackEntryCount >= 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return false
    }

    fun setTreeNavigation() {
        navigationView.menu.getItem(0).isChecked = true
    }

    fun replaceFragment(fragment: Fragment?, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment!!)
        ft.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val internalStorageService = InternalStorageService(this)
        internalStorageService.setiSetInternalData(SetUserIdAndToken())
        internalStorageService.execute()

        bundle = if (intent.extras != null) intent.extras else Bundle()
        stackBundles.add(bundle)
        replaceFragment(TreeFragment.newInstance(bundle), FragmentType.TREE.name)

        navigationView = findViewById(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigationView.setOnNavigationItemReselectedListener(mOnNavigationItemReSelectedListener)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            backFragment()
        }
    }

    fun exit() {
        cleanMainUser()
        val internalStorageService = InternalStorageService(this)
        internalStorageService.setiSetInternalData(ClearUserIdAndToken())
        internalStorageService.execute()
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
        private val stackBundles = ArrayList<Bundle>()

        fun getStackBundles(): MutableList<Bundle> {
            return stackBundles
        }

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
