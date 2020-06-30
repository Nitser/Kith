package com.project.scratchstudio.kith_andoid.ui.home_package.user_change_password

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.databinding.FragmentChangePasswordBinding
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel

class ChangePasswordFragment : BaseFragment() {

    private lateinit var userPresenter: UserPresenter
    private lateinit var binding: FragmentChangePasswordBinding
    private val mainUserViewModel: MainUserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        binding.changePasswordChange.setOnClickListener { onClickChangePassword() }
    }

    private fun onClickChangePassword() {
        with(mainUserViewModel.getMainUser().value!!) {
//            if (binding.changePasswordOldPassword.text.toString() == password) {
                if (binding.changePasswordNewPassword.text.toString() == binding.changePasswordRepeatPassword.text.toString()) {
                    userPresenter.changePassword(object : UserPresenter.BaseCallback {
                        override fun onSuccess(baseResponse: BaseResponse) {
                            password = binding.changePasswordNewPassword.text.toString()
                            mainUserViewModel.setMainUser(this@with)
                            Toast.makeText(context, "Пароль изменен", Toast.LENGTH_SHORT).show()
                            requireActivity().findNavController(R.id.nav_host_fragment_home).popBackStack()
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }, id, password)
                } else {
                    Toast.makeText(context, "Новый пароль введен неверно", Toast.LENGTH_SHORT).show()
                }
//            } else {
//                Toast.makeText(context, "Старый пароль введен неверно", Toast.LENGTH_SHORT).show()
//            }
        }
    }

}
