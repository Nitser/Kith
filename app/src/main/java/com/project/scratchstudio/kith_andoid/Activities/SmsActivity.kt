package com.project.scratchstudio.kith_andoid.Activities

import android.content.Intent
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.Service.HttpService

class SmsActivity : AppCompatActivity() {

    private var httpService: HttpService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)
        httpService = HttpService()
        httpService!!.sendSms(this, HomeActivity.mainUser.login)
    }

    fun onClickBackButton(view: View) {
        view.isEnabled = false
        val intent = Intent(this@SmsActivity, MainActivity::class.java)
        intent.putExtra("another_user", false)
        startActivity(intent)
        finish()
    }

    fun checkButton(view: View) {
        view.isEnabled = false
        val smsCode = findViewById<CustomFontEditText>(R.id.editText7)
        httpService!!.checkSms(this, view, HomeActivity.mainUser.id, smsCode.text.toString())
    }

    fun againButton(view: View) {
        view.isEnabled = false
        httpService = HttpService()
        httpService!!.sendSms(this, HomeActivity.mainUser.login)

        val text = view as CustomFontTextView
        text.setTextColor(resources.getColor(R.color.colorHint))
        object : CountDownTimer(20000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val s = "Отправить повторно через: " + millisUntilFinished / 1000
                text.text = s
            }

            override fun onFinish() {
                text.text = "Отправить повторно"
                text.setTextColor(resources.getColor(R.color.colorAccent))
                text.isEnabled = true
            }
        }.start()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this@SmsActivity, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
