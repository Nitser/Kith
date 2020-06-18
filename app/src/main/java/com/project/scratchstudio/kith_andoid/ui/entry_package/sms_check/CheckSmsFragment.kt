package com.project.scratchstudio.kith_andoid.ui.entry_package.sms_check

import android.accounts.NetworkErrorException
import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentCheckSmsBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse
import com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up.SingUpPresenter

class CheckSmsFragment : BaseFragment() {

    private lateinit var presenter: SingUpPresenter
    private lateinit var binding: FragmentCheckSmsBinding
    private lateinit var editTextBehavior: EditTextBehavior
    private lateinit var user: UserModelView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCheckSmsBinding.inflate(layoutInflater)
        presenter = SingUpPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        user = CheckSmsFragmentArgs.fromBundle(arguments!!).user
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendSMS()
        initButtons(view)
    }

    private fun sendSMS() {
        presenter.sendSMS(object : SingUpPresenter.BaseCallback {
            override fun onSuccess(baseResponse: NewBaseResponse) {
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, user.id)
    }

    private fun initButtons(view: View) {
        binding.next.setOnClickListener(this::checkButton)
        binding.resetConfirmCode.setOnClickListener(this::onClickResetConfirmCode)
    }

    private fun checkButton(view: View) {
        view.isEnabled = false
        presenter.checkSMS(object : SingUpPresenter.BaseCallback {
            override fun onSuccess(baseResponse: NewBaseResponse) {
                activity!!.currentFocus?.clearFocus()
                Toast.makeText(context, "SMS код подтвержден", Toast.LENGTH_SHORT).show()
                val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                view.findNavController().navigate(CheckSmsFragmentDirections
                        .actionCheckSmsFragmentToMainFragment())
            }

            override fun onError(networkError: NetworkErrorException) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }, user.id, binding.confirmCode.text.toString())
    }

    private fun onClickResetConfirmCode(view: View) {
        binding.resetConfirmCode.isEnabled = false
        sendSMS()

        binding.resetConfirmCode.setTextColor(resources.getColor(R.color.colorHint))
        object : CountDownTimer(20000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val s = "Отправить повторно через: " + millisUntilFinished / 1000
                binding.resetConfirmCode.text = s
            }

            override fun onFinish() {
                binding.resetConfirmCode.text = "Отправить код повторно"
                binding.resetConfirmCode.setTextColor(resources.getColor(R.color.colorAccent))
                binding.resetConfirmCode.isEnabled = true
            }
        }.start()

    }

    companion object {

        fun newInstance(): CheckSmsFragment {
            return CheckSmsFragment()
        }
    }
}