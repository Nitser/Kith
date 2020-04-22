package com.project.scratchstudio.kith_andoid.ui.entry_package.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment

class SplashFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = ""
        (activity as AppCompatActivity).supportActionBar!!.setHomeButtonEnabled(false)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    companion object {

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}