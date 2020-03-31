package com.project.scratchstudio.kith_andoid.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.service.internal_storage.InternalStorageService
import com.project.scratchstudio.kith_andoid.service.internal_storage.get_internal_data.GetCountData
import com.project.scratchstudio.kith_andoid.service.internal_storage.get_internal_data.GetUserIdAndToken
import com.project.scratchstudio.kith_andoid.ui.entry_package.main.MainFragment
import com.project.scratchstudio.kith_andoid.ui.entry_package.splash.SplashFragment

class EntryActivity : BaseActivity() {

    private val SPLASH_DISPLAY_LENGTH = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        replaceFragment(SplashFragment.newInstance(), FragmentType.SPLASH.name)
        loadUserFromStorage()
        loadCountFromStorage()
    }

    private fun loadUserFromStorage() {
        val getUser = InternalStorageService(this, object : InternalStorageService.PostExecuteCallback {
            override fun doAfter() {
                if (InternalStorageService.entryStatus) {
                    Handler().postDelayed({
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.putExtra("another_user", false)
                        startActivity(intent)
                        finish()
                    }, SPLASH_DISPLAY_LENGTH.toLong())
                } else {
                    Handler().postDelayed({
                        replaceFragment(MainFragment.newInstance(), FragmentType.MAIN.name)
//                        val intent = Intent(applicationContext, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
                    }, SPLASH_DISPLAY_LENGTH.toLong())
                }
            }
        })
        getUser.setIGetInternalData(GetUserIdAndToken())
        getUser.execute()
    }

    private fun loadCountFromStorage() {
        val getCount = InternalStorageService(this, null)
        getCount.setIGetInternalData(GetCountData())
        getCount.execute()
    }

}