package com.project.scratchstudio.kith_andoid.ui.entry_package.password_recovery

import android.accounts.NetworkErrorException
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentPasswordRecoveryEnterNewPasswordBinding
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse

class PasswordRecoveryEnterNewPasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentPasswordRecoveryEnterNewPasswordBinding
    private lateinit var presenter: PasswordRecoveryPresenter
    private lateinit var editTextBehavior: EditTextBehavior
    private var userId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.password_recovery)
        binding = FragmentPasswordRecoveryEnterNewPasswordBinding.inflate(layoutInflater)
        binding.next.setOnClickListener(this::onClickNext)
        presenter = PasswordRecoveryPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        userId = PasswordRecoveryEnterNewPasswordFragmentArgs.fromBundle(arguments!!).userId
        return binding.root
    }

    private fun onClickNext(view: View) {
        if (checkLegalPassword()) {
            presenter.changeNewPassword(object : PasswordRecoveryPresenter.SendCodeCallback {
                override fun onSuccess(baseResponse: NewBaseResponse) {
                    activity!!.currentFocus?.clearFocus()
                    Toast.makeText(context, "Пароль изменен", Toast.LENGTH_SHORT).show()
                    val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    view.findNavController().navigate(PasswordRecoveryEnterNewPasswordFragmentDirections
                            .actionPasswordRecoveryEnterNewPasswordFragmentToSingInFragment())
                }

                override fun onError(networkError: NetworkErrorException) {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
            }, userId, binding.newPassword.text.toString(), binding.confirmPassword.text.toString())
        }
    }

    private fun checkLegalPassword(): Boolean {
        return if (editTextBehavior.notEmptyField(binding.newPassword) && checkPasswordLength()) {
            editTextBehavior.equalsFieldsWithText(binding.newPassword, binding.confirmPassword, "Пароли не совпадают")
        } else {
            false
        }
    }

    private fun checkPasswordLength(): Boolean {
        return if (binding.newPassword.length() >= 6) {
            true
        } else {
            editTextBehavior.fieldErrorWithText(binding.newPassword, "Пароль короче 6 символов")
            return false
        }
    }

    companion object {

        fun newInstance(): PasswordRecoveryEnterNewPasswordFragment {
            return PasswordRecoveryEnterNewPasswordFragment()
        }
    }
}