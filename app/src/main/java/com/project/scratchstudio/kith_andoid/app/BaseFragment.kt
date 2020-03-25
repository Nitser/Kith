package com.project.scratchstudio.kith_andoid.app

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    open fun onBackPressed(): Boolean {
        return false
    }
}
