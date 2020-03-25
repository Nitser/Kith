package com.project.scratchstudio.kith_andoid.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.project.scratchstudio.kith_andoid.GetInternalData.GetCountData
import com.project.scratchstudio.kith_andoid.GetInternalData.GetUserIdAndToken
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.Service.InternalStorageService

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val getCount = InternalStorageService(this)
        getCount.setiGetInternalData(GetCountData())
        getCount.execute()


        val getUser = InternalStorageService(this)
        getUser.setiGetInternalData(GetUserIdAndToken())
        getUser.execute()

    }
}
