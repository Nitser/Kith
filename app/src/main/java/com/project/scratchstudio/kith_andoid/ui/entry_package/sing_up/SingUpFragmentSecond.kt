package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpSecondBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse

class SingUpFragmentSecond : BaseFragment() {

    private lateinit var presenter: SingUpPresenter
    private lateinit var binding: FragmentSingUpSecondBinding
    private lateinit var user: UserModelView
    private lateinit var editTextBehavior: EditTextBehavior

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpSecondBinding.inflate(layoutInflater)
        initButtons()
        presenter = SingUpPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        user = SingUpFragmentSecondArgs.fromBundle(arguments!!).user
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.registration)
        return binding.root
    }

    private fun initButtons() {
        binding.next.setOnClickListener(this::onClickNext)
    }

    private fun onClickNext(view: View) {
        if (checkNotEmptyImportantFields()) {
            presenter.checkField(object : SingUpPresenter.GetUserCallback {
                override fun onSuccess(baseResponse: NewBaseResponse) {
                    presenter.checkField(object : SingUpPresenter.GetUserCallback {
                        override fun onSuccess(baseResponse: NewBaseResponse) {
                            if (checkPasswordLength()) {
                                if (editTextBehavior.equalsFieldsWithText(binding.password, binding.doublePassword, "Пароли не совпадают")) {
                                    parseUser()
                                    view.findNavController().navigate(SingUpFragmentSecondDirections.actionSingUpFragmentSecondToSingUpFragmentThird(user))
                                }
                            } else {
                                editTextBehavior.fieldErrorWithText(binding.password, "Пароль короче 6 символов")
                            }
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            editTextBehavior.fieldErrorWithText(binding.login, "Логин уже занят")
                        }
                    },"login", binding.login.text.toString() )
                }

                override fun onError(networkError: NetworkErrorException) {
                    editTextBehavior.fieldErrorWithText(binding.email, "Email уже занят")
                }
            }, "email", binding.email.text.toString())
        }
    }

    private fun checkNotEmptyImportantFields(): Boolean {
        return editTextBehavior.notEmptyField(binding.login).and(editTextBehavior.notEmptyField(binding.email))
                .and(editTextBehavior.notEmptyField(binding.password))
    }

    private fun checkPasswordLength(): Boolean {
        return (binding.password.length() >= 6)
    }

    private fun parseUser(): UserModelView {
        with(user) {
            login = binding.login.text.toString()
            email = binding.email.text.toString()
            password = binding.password.text.toString()
        }
        return user
    }

    companion object {

        fun newInstance(): SingUpFragmentSecond {
            return SingUpFragmentSecond()
        }
    }
}