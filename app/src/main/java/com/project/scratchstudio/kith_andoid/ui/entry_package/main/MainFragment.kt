package com.project.scratchstudio.kith_andoid.ui.entry_package.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.project.scratchstudio.kith_andoid.Activities.CheckInActivity
import com.project.scratchstudio.kith_andoid.Activities.SignInActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Permissions

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
        view.isEnabled = false
        val intent = Intent(activity, SignInActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun singUpButton(view: View) {
        view.isEnabled = false
        val intent = Intent(activity, CheckInActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}