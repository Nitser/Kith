package com.project.scratchstudio.kith_andoid.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button

import com.project.scratchstudio.kith_andoid.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 23) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }

        //        CustomFontTextView counts = findViewById(R.id.customFontTextView2);
        //        counts.setTypeface(null, Typeface.BOLD);

        //        if(isNetworkConnected()){
        //            HttpService httpService = new HttpService();
        //            httpService.count(this, counts);
        //
        //        } else {
        //            counts.setText(String.valueOf(Cache.getAllUsers()));
        //            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
        //        }


        val button = findViewById<Button>(R.id.button)
        val button1 = findViewById<Button>(R.id.button2)
        button.typeface = Typeface.createFromAsset(assets, "fonts/intro_regular.ttf")
        button1.typeface = Typeface.createFromAsset(assets, "fonts/intro_regular.ttf")
    }

    fun signInButton(view: View) {
        view.isEnabled = false
        val intent = Intent(this@MainActivity, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun registrationButton(view: View) {
        view.isEnabled = false
        val intent = Intent(this@MainActivity, CheckInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            moveTaskToBack(true)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}
