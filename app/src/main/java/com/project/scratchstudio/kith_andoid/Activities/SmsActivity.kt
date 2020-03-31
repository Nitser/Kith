package com.project.scratchstudio.kith_andoid.Activities

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView
import com.project.scratchstudio.kith_andoid.EntryPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.service.HttpService
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse

class SmsActivity : AppCompatActivity() {

    private var httpService: HttpService? = null
    private lateinit var entryPresenter: EntryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryPresenter = EntryPresenter(applicationContext)
        setContentView(R.layout.activity_sms)
        sendSMS()
    }

    private fun sendSMS() {
        entryPresenter.sendSMS(object : EntryPresenter.BaseCallback {
            override fun onSuccess(baseResponse: BaseResponse) {
                if (!baseResponse.status) {
                    Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, HomeActivity.mainUser.login)
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
        entryPresenter.checkSMS(object : EntryPresenter.EntryCallback {
            override fun onSuccess(entryResponse: EntryResponse) {
                if (entryResponse.status) {
                    HomeActivity.mainUser.token = entryResponse.tokenType + " " + entryResponse.token
                    val intent = Intent(applicationContext, HomeActivity::class.java)
//                    intent.putExtra("another_user", false)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "SMS код подтвержден", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    view.isEnabled = true
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                view.isEnabled = true
            }
        }, HomeActivity.mainUser.login, HomeActivity.mainUser.password, smsCode.text.toString())
    }

    fun againButton(view: View) {
        view.isEnabled = false
        sendSMS()

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
