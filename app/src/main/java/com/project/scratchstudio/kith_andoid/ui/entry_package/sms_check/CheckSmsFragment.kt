package com.project.scratchstudio.kith_andoid.ui.entry_package.sms_check

import android.accounts.NetworkErrorException
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.project.scratchstudio.kith_andoid.activities.EntryActivity
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontEditText
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontTextView
import com.project.scratchstudio.kith_andoid.EntryPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse

class CheckSmsFragment : BaseFragment() {

    private lateinit var entryPresenter: EntryPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entryPresenter = EntryPresenter(context!!)
        return inflater.inflate(R.layout.fragment_check_sms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendSMS()
        initButtons(view)
    }

    private fun sendSMS() {
        entryPresenter.sendSMS(object : EntryPresenter.BaseCallback {
            override fun onSuccess(baseResponse: BaseResponse) {
                if (!baseResponse.status) {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, HomeActivity.mainUser.login)
    }

    private fun initButtons(view: View) {
        view.findViewById<TextView>(R.id.check_sms_back).setOnClickListener(this::onClickBackButton)
        view.findViewById<Button>(R.id.check_sms_confirm).setOnClickListener(this::checkButton)
        view.findViewById<TextView>(R.id.check_sms_repeat).setOnClickListener(this::againButton)
    }

    fun onClickBackButton(view: View) {
        view.isEnabled = false
        (activity as EntryActivity).backFragment()
    }

    fun checkButton(view: View) {
        view.isEnabled = false
        val smsCode = view.findViewById<CustomFontEditText>(R.id.editText7)
        entryPresenter.checkSMS(object : EntryPresenter.EntryCallback {
            override fun onSuccess(entryResponse: EntryResponse) {
                if (entryResponse.status) {
                    HomeActivity.mainUser.token = entryResponse.tokenType + " " + entryResponse.token
                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(context, "SMS код подтвержден", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                } else {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    view.isEnabled = true
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
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

    companion object {

        fun newInstance(): CheckSmsFragment {
            return CheckSmsFragment()
        }
    }
}