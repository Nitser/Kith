package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment

class SingUpShowPermissionFragment : BaseFragment() {

    private lateinit var webLink: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.registration)
        webLink = SingUpShowPermissionFragmentArgs.fromBundle(arguments!!).webLink
        return inflater.inflate(R.layout.fragment_sing_up_show_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView: WebView = view.findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(webLink)
    }

    companion object {

        fun newInstance(): SingUpShowPermissionFragment {
            return SingUpShowPermissionFragment()
        }
    }
}