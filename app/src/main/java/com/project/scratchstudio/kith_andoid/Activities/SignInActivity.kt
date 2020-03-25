package com.project.scratchstudio.kith_andoid.Activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class SignInActivity : AppCompatActivity() {

    private lateinit var entryApi: EntryApi
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        entryApi = ApiClient.getClient(applicationContext).create<EntryApi>(EntryApi::class.java)

        val button = findViewById<Button>(R.id.button2)
        button.typeface = Typeface.createFromAsset(assets, "fonts/intro_regular.ttf")
    }

    fun signInButton(view: View) {
        view.isEnabled = false
        val login = findViewById<CustomFontEditText>(R.id.editText2)
        val password = findViewById<CustomFontEditText>(R.id.editText3)
        HomeActivity.cleanMainUser()

        disposable.add(
                entryApi.singIn(login.text.toString(), password.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<EntryResponse>() {
                            override fun onSuccess(response: EntryResponse) {
                                if (response.status) {
                                    HomeActivity.createMainUser()
                                    HomeActivity.mainUser.id = response.userId
                                    HomeActivity.mainUser.token = response.tokenType + " " + response.token
                                    HomeActivity.mainUser.password = password.text.toString()
                                    val intent = Intent(view.context, HomeActivity::class.java)
                                    intent.putExtra("another_user", false)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                }
                                view.isEnabled = true
                            }

//                            else if (code == 405) {
//                                response = JSONObject(resultJSON)
//                                HomeActivity.createMainUser()
//                                HomeActivity.mainUser!!.login = login
//                                HomeActivity.mainUser!!.password = password
//                                if (view.context != null) {
//                                    val intent = Intent(view.context, SmsActivity::class.java)
//                                    view.context.startActivity(intent)
//                                }

                            override fun onError(e: Throwable) {
                                Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                view.isEnabled = true
                            }
                        })
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    fun onClickBack(view: View) {
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
