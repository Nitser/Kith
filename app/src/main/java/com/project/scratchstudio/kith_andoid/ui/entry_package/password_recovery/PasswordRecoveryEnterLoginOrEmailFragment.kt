package com.project.scratchstudio.kith_andoid.ui.entry_package.password_recovery

import android.accounts.NetworkErrorException
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentPasswordRecoveryEnterLoginOrEmailBinding
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse

class PasswordRecoveryEnterLoginOrEmailFragment : BaseFragment() {

    private lateinit var binding: FragmentPasswordRecoveryEnterLoginOrEmailBinding
    private lateinit var presenter: PasswordRecoveryPresenter
    private lateinit var editTextBehavior: EditTextBehavior

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPasswordRecoveryEnterLoginOrEmailBinding.inflate(layoutInflater)
        binding.next.setOnClickListener(this::onClickNext)
        presenter = PasswordRecoveryPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        return binding.root
    }

    private fun onClickNext(view: View) {
        presenter.sendConfirmCode(object : PasswordRecoveryPresenter.SendCodeCallback {
            override fun onSuccess(baseResponse: NewBaseResponse) {
                val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                view.findNavController().navigate(PasswordRecoveryEnterLoginOrEmailFragmentDirections
                        .actionPasswordRecoveryEnterLoginOrEmailFragmentToPasswordRecoveryEnterConfirmCodeFragment(baseResponse.userId
                                , binding.loginOrEmail.text.toString()))
            }

            override fun onError(networkError: NetworkErrorException) {
                editTextBehavior.fieldErrorWithText(binding.loginOrEmail, "Логин или email не найден")
            }
        }, binding.loginOrEmail.text.toString())
    }

    companion object {

        fun newInstance(): PasswordRecoveryEnterLoginOrEmailFragment {
            return PasswordRecoveryEnterLoginOrEmailFragment()
        }
    }
}