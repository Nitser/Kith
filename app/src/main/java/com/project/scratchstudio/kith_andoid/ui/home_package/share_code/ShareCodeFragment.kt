package com.project.scratchstudio.kith_andoid.ui.home_package.share_code

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import com.project.scratchstudio.kith_andoid.ui.entry_package.splash.SplashFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ShareCodeFragment : BaseFragment() {

    private lateinit var referralCode: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        referralCode = ShareCodeFragmentArgs.fromBundle(arguments!!).referralCode
        return inflater.inflate(R.layout.fragment_share_code, container, false)
    }

    companion object {

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_share_code, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share_code_share -> {
                shareButton()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.findViewById<ImageButton>(R.id.share_code_button_copy).setOnClickListener(this::copyButton)
        activity!!.findViewById<TextView>(R.id.share_code_ref_code).text = referralCode
//        HomeActivity.disposable.add(
//                HomeActivity.userApi.getReferralCode(HomeActivity.mainUser.id)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(object : DisposableSingleObserver<ReferralResponse>() {
//                            override fun onSuccess(response: ReferralResponse) {
//                                if (response.status) {
//                                    val textView = activity!!.findViewById<TextView>(R.id.share_code_ref_code)
//                                    textView.text = response.referral
//                                } else {
//                                    Toast.makeText(requireContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//
//                            override fun onError(e: Throwable) {
//                                Toast.makeText(requireContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
//                            }
//                        })
//        )
    }

    private fun copyButton(view: View) {
        val textView = activity!!.findViewById<TextView>(R.id.share_code_ref_code)
        val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ref_code", textView.text.toString())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Код сохранен", Toast.LENGTH_SHORT).show()
    }

    private fun shareButton() {
        val code = activity!!.findViewById<TextView>(R.id.share_code_ref_code)
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, code.text.toString())
        sendIntent.type = "text/plain"
        startActivityForResult(sendIntent, 0)
    }

}
