package com.project.scratchstudio.kith_andoid.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.utils.internal_storage.InternalStorageService
import com.project.scratchstudio.kith_andoid.utils.internal_storage.get_internal_data.GetUserIdAndToken

class EntryActivity : BaseActivity() {

    private val SPLASH_DISPLAY_LENGTH = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        loadUserFromStorage()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }

    private fun loadUserFromStorage() {
        val getUser = InternalStorageService(this, object : InternalStorageService.PostExecuteCallback {
            override fun doAfter() {
                if (InternalStorageService.entryStatus) {
                    Handler().postDelayed({
                        Const.token = InternalStorageService.user.token
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.putExtra("user", InternalStorageService.user)
                        startActivity(intent)
                        finish()
                    }, SPLASH_DISPLAY_LENGTH.toLong())
                } else {
                    Handler().postDelayed({
                        findNavController(R.id.nav_host_fragment).navigate(R.id.action_splashFragment_to_mainFragment)
                    }, SPLASH_DISPLAY_LENGTH.toLong())
                }
            }
        })
        getUser.setIGetInternalData(GetUserIdAndToken())
        getUser.execute()
    }

}