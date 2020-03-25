package com.project.scratchstudio.kith_andoid.Activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CodeActivity : AppCompatActivity() {

    private var share: ImageButton? = null
    private var userPresenter: UserPresenter? = null
    private val disposable = CompositeDisposable()
    private var userApi: UserApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)
        userPresenter = UserPresenter(applicationContext)
        userApi = ApiClient.getClient(applicationContext).create<UserApi>(UserApi::class.java)

        disposable.add(
                userApi!!.getReferralCode(HomeActivity.mainUser.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<ReferralResponse>() {
                            override fun onSuccess(response: ReferralResponse) {
                                if (response.status) {
                                    val textView = findViewById<CustomFontTextView>(R.id.customFontTextView6)
                                    textView.text = response.referral
                                } else {
                                    Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        })
        )
    }

    fun onClickBackButton(view: View) {
        view.isEnabled = false
        finish()
    }

    fun copyButton(view: View) {
        val textView = findViewById<TextView>(R.id.customFontTextView6)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("code", textView.text.toString())
        clipboard.primaryClip = clip
        Toast.makeText(this, "Код сохранен", Toast.LENGTH_SHORT).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    fun shareButton(view: View) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return
        }
        buttonCount = SystemClock.elapsedRealtime()
        view.isEnabled = false
        share = view as ImageButton
        val code = findViewById<TextView>(R.id.customFontTextView6)
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, code.text.toString())
        sendIntent.type = "text/plain"
        startActivityForResult(sendIntent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == 0)
            share!!.isEnabled = true
    }

    companion object {
        private var buttonCount: Long = 0
    }

}
