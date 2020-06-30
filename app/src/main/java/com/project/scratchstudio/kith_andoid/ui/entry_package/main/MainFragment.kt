package com.project.scratchstudio.kith_andoid.ui.entry_package.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment

class MainFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = ""
        (activity as AppCompatActivity).supportActionBar!!.setHomeButtonEnabled(false)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons(view)
    }

    private fun initButtons(view: View) {
        view.findViewById<Button>(R.id.sing_in).setOnClickListener(
                Navigation.createNavigateOnClickListener(MainFragmentDirections.actionMainFragmentToSingInFragment())
        )
        view.findViewById<Button>(R.id.sing_up).setOnClickListener(
                Navigation.createNavigateOnClickListener(MainFragmentDirections.actionMainFragmentToSingUpRefCode())
        )
    }

    companion object {

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}