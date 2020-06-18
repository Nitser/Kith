package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

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
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpRefCodeBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse

class SingUpRefCodeFragment : BaseFragment() {

    private lateinit var binding: FragmentSingUpRefCodeBinding
    private lateinit var editTextBehavior: EditTextBehavior
    private lateinit var presenter: SingUpPresenter
    private val user = UserModelView()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpRefCodeBinding.inflate(layoutInflater)
        editTextBehavior = EditTextBehavior(context!!)
        presenter = SingUpPresenter(context!!)
        initButtons()
        return binding.root
    }

    private fun initButtons() {
        binding.next.setOnClickListener(this::onClickNext)
    }

    private fun onClickNext(view: View) {
        checkReferralCode(view)
    }

    private fun checkReferralCode(view: View) {
        presenter.checkReferralCode(object : SingUpPresenter.GetUserCallback {
            override fun onSuccess(baseResponse: NewBaseResponse) {
                val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                user.invitedUserId = baseResponse.userId
                view.findNavController().navigate(SingUpRefCodeFragmentDirections.actionSingUpRefCodeToSingUpFragment(user))
            }

            override fun onError(networkError: NetworkErrorException) {
                editTextBehavior.fieldErrorWithText(binding.refCode, "Реферальный код не найден")
            }
        }, binding.refCode.text.toString())
    }

    companion object {

        fun newInstance(): SingUpRefCodeFragment {
            return SingUpRefCodeFragment()
        }
    }
}