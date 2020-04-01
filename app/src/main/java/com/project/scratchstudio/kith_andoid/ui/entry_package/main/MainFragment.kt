package com.project.scratchstudio.kith_andoid.ui.entry_package.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.project.scratchstudio.kith_andoid.Activities.EntryActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.app.Permissions
import com.project.scratchstudio.kith_andoid.ui.entry_package.sing_in.SingInFragment
import com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up.SingUpFragment

class MainFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Permissions.readExternalStorage(context!!, activity!!)
        initButtons(view)
    }

    private fun initButtons(view: View) {
        val singIn: Button = view.findViewById(R.id.sing_in)
        val singUp: Button = view.findViewById(R.id.sing_up)
        singIn.setOnClickListener(this::signInButton)
        singUp.setOnClickListener(this::singUpButton)
    }

    private fun signInButton(view: View) {
        (activity as EntryActivity).addFragment(SingInFragment.newInstance(), FragmentType.SING_IN.name)
    }

    private fun singUpButton(view: View) {
        (activity as EntryActivity).addFragment(SingUpFragment.newInstance(), FragmentType.SING_UP.name)
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}