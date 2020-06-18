package com.project.scratchstudio.kith_andoid.ui.home_package.user_change_password

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.databinding.FragmentChangePasswordBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import java.util.Objects

class ChangePasswordFragment : BaseFragment() {

    private lateinit var userPresenter: UserPresenter
    private lateinit var binding: FragmentChangePasswordBinding
    private val mainUserViewModel: MainUserViewModel by activityViewModels()
    private lateinit var user: UserModelView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        user = mainUserViewModel.getMainUser().value!!
        binding.changePasswordChange.setOnClickListener(this::onClickChangePassword)
    }

    fun onClickChangePassword(view: View) {
        view.isEnabled = false
        if (Objects.requireNonNull<Editable>(binding.changePasswordOldPassword.text).toString() == user.password) {

            if (Objects.requireNonNull<Editable>(binding.changePasswordNewPassword.text).toString() == Objects.requireNonNull<Editable>(binding.changePasswordRepeatPassword.text).toString()) {
                userPresenter.changePassword(object : UserPresenter.BaseCallback {
                    override fun onSuccess(baseResponse: BaseResponse) {
                        if (baseResponse.status) {
                            user.password = binding.changePasswordNewPassword.text.toString()
                            mainUserViewModel.setMainUser(user)
                            Toast.makeText(context, "Пароль изменен", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                        view.isEnabled = true
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        view.isEnabled = true
                    }
                }, user.id, user.password, binding.changePasswordNewPassword.text.toString())
            } else {
                view.isEnabled = true
                Toast.makeText(context, "Новый пароль введен неверно", Toast.LENGTH_SHORT).show()
            }
        } else {
            view.isEnabled = true
            Toast.makeText(context, "Старый пароль введен неверно", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(): ChangePasswordFragment {
            return ChangePasswordFragment()
        }
    }

}
