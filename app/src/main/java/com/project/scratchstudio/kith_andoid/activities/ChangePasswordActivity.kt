package com.project.scratchstudio.kith_andoid.activities

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontEditText
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import java.util.Objects

class ChangePasswordActivity : AppCompatActivity() {

    private var isChange = false
    private lateinit var userPresenter: UserPresenter

    private val isNetworkConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        userPresenter = UserPresenter(applicationContext)
    }

    fun onClickChangePassword(view: View) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return
        }
        buttonCount = SystemClock.elapsedRealtime()
        view.isEnabled = false
        val oldPassword = findViewById<CustomFontEditText>(R.id.old_password)
        if (Objects.requireNonNull<Editable>(oldPassword.text).toString() == HomeActivity.mainUser.password) {
            val newPassword = findViewById<CustomFontEditText>(R.id.new_password)
            val repPassword = findViewById<CustomFontEditText>(R.id.rep_password)
            if (Objects.requireNonNull<Editable>(newPassword.text).toString() == Objects.requireNonNull<Editable>(repPassword.text).toString()) {
                if (isNetworkConnected) {
                    userPresenter.changePassword(object : UserPresenter.BaseCallback {
                        override fun onSuccess(baseResponse: BaseResponse) {
                            if (baseResponse.status) {
                                Toast.makeText(applicationContext, "Пароль изменен", Toast.LENGTH_SHORT).show()
                                saveNewPassword(newPassword.text.toString())
                            } else {
                                Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                            view.isEnabled = true
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            view.isEnabled = true
                        }
                    }, HomeActivity.mainUser.id, HomeActivity.mainUser.password, newPassword.text.toString())
                } else {
                    view.isEnabled = true
                    Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show()
                }
            } else {
                view.isEnabled = true
                Toast.makeText(this, "Новый пароль введен неверно", Toast.LENGTH_SHORT).show()
            }
        } else {
            view.isEnabled = true
            Toast.makeText(this, "Старый пароль введен неверно", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveNewPassword(password: String) {
        val button = findViewById<Button>(R.id.change)
        button.isEnabled = true
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val ed: SharedPreferences.Editor
        ed = sp.edit()
        ed.putString("cur_user_password", password)
        ed.apply()

        HomeActivity.mainUser.password = password
        isChange = true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this@ChangePasswordActivity, EditActivity::class.java)
            if (isChange)
                setResult(PASSWORD_CHANGED, intent)
            else
                setResult(PASSWORD_NOT_CHANGED, intent)
            finish()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    fun onClickCancel() {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
        buttonCount = SystemClock.elapsedRealtime()
        val intent = Intent(this@ChangePasswordActivity, EditActivity::class.java)
        if (isChange)
            setResult(PASSWORD_CHANGED, intent)
        else
            setResult(PASSWORD_NOT_CHANGED, intent)
        finish()
    }

    fun onClickDone() {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
        buttonCount = SystemClock.elapsedRealtime()
        val intent = Intent(this@ChangePasswordActivity, EditActivity::class.java)
        if (isChange)
            setResult(PASSWORD_CHANGED, intent)
        else
            setResult(PASSWORD_NOT_CHANGED, intent)
        finish()
    }

    companion object {
        private val PASSWORD_CHANGED = 0
        private val PASSWORD_NOT_CHANGED = 1
        private var buttonCount: Long = 0
    }

}
