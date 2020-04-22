package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_in

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.activities.EntryActivity
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontEditText
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse
import com.project.scratchstudio.kith_andoid.ui.entry_package.main.MainFragmentDirections
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class SingInFragment : BaseFragment() {

    private lateinit var entryApi: EntryApi
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entryApi = ApiClient.getClient(context!!).create<EntryApi>(EntryApi::class.java)
        (activity as AppCompatActivity).supportActionBar!!.title = ""
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons(view)
    }

    private fun initButtons(view: View) {
        view.findViewById<Button>(R.id.sing_in).setOnClickListener(this::signInButton)
        view.findViewById<TextView>(R.id.forgotten_password).setOnClickListener(
                Navigation.createNavigateOnClickListener(SingInFragmentDirections.actionSingInFragmentToPasswordRecoveryEnterLoginOrEmailFragment())
        )
    }

    fun signInButton(view: View) {
        view.isEnabled = false
        val login = view.findViewById<CustomFontEditText>(R.id.editText2)
        val password = view.findViewById<CustomFontEditText>(R.id.editText3)
        HomeActivity.cleanMainUser()

        disposable.add(
                entryApi.singIn(login.text.toString(), password.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<EntryResponse>() {
                            override fun onSuccess(response: EntryResponse) {
                                if (response.status) {
                                    HomeActivity.createMainUser()
                                    HomeActivity.mainUser.id = response.userId
                                    HomeActivity.mainUser.token = response.tokenType + " " + response.token
                                    HomeActivity.mainUser.password = password.text.toString()
                                    val intent = Intent(view.context, HomeActivity::class.java)
                                    intent.putExtra("another_user", false)
                                    startActivity(intent)
                                    activity?.finish()
                                } else {
                                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                }
                                view.isEnabled = true
                            }

//                            else if (code == 405) {
//                                response = JSONObject(resultJSON)
//                                HomeActivity.createMainUser()
//                                HomeActivity.mainUser!!.login = login
//                                HomeActivity.mainUser!!.password = password
//                                if (view.context != null) {
//                                    val intent = Intent(view.context, SmsActivity::class.java)
//                                    view.context.startActivity(intent)
//                                }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                view.isEnabled = true
                            }
                        })
        )
    }

    fun onClickBack(view: View) {
        (activity as EntryActivity).backFragment()
    }

    companion object {

        fun newInstance(): SingInFragment {
            return SingInFragment()
        }
    }
}