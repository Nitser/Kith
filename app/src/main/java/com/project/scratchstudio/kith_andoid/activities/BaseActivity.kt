package com.project.scratchstudio.kith_andoid.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    protected var bundle: Bundle? = null

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
}