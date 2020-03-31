package com.project.scratchstudio.kith_andoid.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissions {

    companion object {
        fun readExternalStorage(context: Context, activity: Activity) {
            if (Build.VERSION.SDK_INT >= 23) {
                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }
            }
        }
    }

}