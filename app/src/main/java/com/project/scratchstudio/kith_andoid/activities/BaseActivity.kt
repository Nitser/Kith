package com.project.scratchstudio.kith_andoid.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.appbar.AppBarLayout
import com.project.scratchstudio.kith_andoid.R
import java.util.ArrayList

open class BaseActivity : AppCompatActivity() {
    protected var bundle: Bundle? = null
    protected lateinit var fragmentTransaction: FragmentTransaction

    companion object {
        val stackBundles = ArrayList<Bundle>()
    }

    open fun getStackBundles(): MutableList<Bundle> {
        return stackBundles
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            backFragment()
        }
    }

    fun backFragment(): Boolean {
        if (supportFragmentManager.backStackEntryCount >= 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return false
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

    fun replaceFragment(fragment: Fragment?, tag: String) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(R.id.container, fragment!!)
        ft.commit()
    }

    fun removeFragment(fragment: Fragment, tag: String) {
        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.remove(fragment)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }
}