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
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpRefCodeBinding
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse

class SingUpRefCodeFragment : BaseFragment() {

    private lateinit var binding: FragmentSingUpRefCodeBinding
    private lateinit var editTextBehavior: EditTextBehavior
    private lateinit var presenter: SingUpPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpRefCodeBinding.inflate(layoutInflater)
        editTextBehavior = EditTextBehavior(context!!)
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.registration)
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
                view.findNavController().navigate(SingUpRefCodeFragmentDirections.actionSingUpRefCodeToSingUpFragment())
            }

            override fun onError(networkError: NetworkErrorException) {
                editTextBehavior.fieldErrorWithText(binding.refCode, "Реферальный код не найден")
            }
        },binding.refCode.text.toString())
    }

    companion object {

        fun newInstance(): SingUpRefCodeFragment {
            return SingUpRefCodeFragment()
        }
    }
}