package com.project.scratchstudio.kith_andoid.ui.entry_package.password_recovery

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
import com.project.scratchstudio.kith_andoid.databinding.FragmentPasswordRecoveryEnterConfirmCodeBinding
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse

class PasswordRecoveryEnterConfirmCodeFragment : BaseFragment() {

    private lateinit var binding: FragmentPasswordRecoveryEnterConfirmCodeBinding
    private lateinit var presenter: PasswordRecoveryPresenter
    private lateinit var editTextBehavior: EditTextBehavior
    private var userId: Int = 0
    private lateinit var userLoginOrEmail: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPasswordRecoveryEnterConfirmCodeBinding.inflate(layoutInflater)
        presenter = PasswordRecoveryPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        binding.next.setOnClickListener(this::onClickNext)
        binding.resetConfirmCode.setOnClickListener(this::onClickResetConfirmCode)
        userId = PasswordRecoveryEnterConfirmCodeFragmentArgs.fromBundle(arguments!!).userId
        userLoginOrEmail = PasswordRecoveryEnterConfirmCodeFragmentArgs.fromBundle(arguments!!).loginOrEmail
        return binding.root
    }

    private fun onClickNext(view: View) {
        presenter.checkConfirmCode(object : PasswordRecoveryPresenter.SendCodeCallback {
            override fun onSuccess(baseResponse: NewBaseResponse) {
                val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                view.findNavController().navigate(PasswordRecoveryEnterConfirmCodeFragmentDirections
                        .actionPasswordRecoveryEnterConfirmCodeFragmentToPasswordRecoveryEnterNewPasswordFragment(userId))
            }

            override fun onError(networkError: NetworkErrorException) {
                editTextBehavior.fieldErrorWithText(binding.confirmCode, "Код подтверждения неправильный")
            }
        }, userId, binding.confirmCode.text.toString())
    }

    private fun resetConfirmCode() {
        presenter.sendConfirmCode(object : PasswordRecoveryPresenter.SendCodeCallback {
            override fun onSuccess(baseResponse: NewBaseResponse) {
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, userLoginOrEmail)
    }

    private fun onClickResetConfirmCode(view: View) {
        binding.resetConfirmCode.isEnabled = false
        resetConfirmCode()

        binding.resetConfirmCode.setTextColor(resources.getColor(R.color.colorHint))
        object : CountDownTimer(20000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val s = "Отправить повторно через: " + millisUntilFinished / 1000
                binding.resetConfirmCode.text = s
            }

            override fun onFinish() {
                if (binding.resetConfirmCode.isAttachedToWindow) {
                    binding.resetConfirmCode.text = "Отправить код повторно"
                    binding.resetConfirmCode.setTextColor(resources.getColor(R.color.colorAccent))
                    binding.resetConfirmCode.isEnabled = true
                }

            }
        }.start()

    }

    companion object {

        fun newInstance(): PasswordRecoveryEnterConfirmCodeFragment {
            return PasswordRecoveryEnterConfirmCodeFragment()
        }
    }
}