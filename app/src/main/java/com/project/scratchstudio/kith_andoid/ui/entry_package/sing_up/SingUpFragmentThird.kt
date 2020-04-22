package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpThirdBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView

class SingUpFragmentThird : BaseFragment() {

    private lateinit var binding: FragmentSingUpThirdBinding
    private lateinit var user: UserModelView
    private lateinit var editTextBehavior: EditTextBehavior

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpThirdBinding.inflate(layoutInflater)
        initButtons()
        editTextBehavior = EditTextBehavior(context!!)
        user = SingUpFragmentThirdArgs.fromBundle(arguments!!).user
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.registration)
        return binding.root
    }

    private fun initButtons() {
        binding.next.setOnClickListener(this::onClickNext)
    }

    private fun onClickNext(view: View) {
        if (checkImportantFields()) {
            parseUser()
//            view.findNavController().navigate(R.id.act, bundle)
        }
    }

    private fun checkImportantFields(): Boolean {
        return editTextBehavior.notEmptyField(binding.phone)
    }

    private fun parseUser(): UserModelView {
        with(user) {
            country = binding.city.text.toString()
            region = binding.region.text.toString()
            city = binding.city.text.toString()
            phone = binding.phone.text.toString()
        }
        return user
    }

    companion object {

        fun newInstance(): SingUpFragmentFirst {
            return SingUpFragmentFirst()
        }
    }
}