package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.user.UserSingIn
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class SingInFragment : BaseFragment() {

    private lateinit var entryApi: EntryApi
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entryApi = ApiClient.getClient(context!!).create<EntryApi>(EntryApi::class.java)
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

    private fun signInButton(view: View) {
        view.isEnabled = false
        val login = activity!!.findViewById<EditText>(R.id.login)
        val password = activity!!.findViewById<EditText>(R.id.password)

        disposable.add(
                entryApi.singIn(login.text.toString(), password.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<UserSingIn>() {
                            override fun onSuccess(response: UserSingIn) {
                                Const.token = response.token
                                Log.i("TEST_ENTRY", "SI: token = ${Const.token}")
                                val user = UserModelView()
                                user.id = response.id
                                user.token = response.token
                                user.password = password.text.toString()
                                val intent = Intent(view.context, HomeActivity::class.java)
                                intent.putExtra("user", user)
                                startActivity(intent)
                                activity?.finish()
                                view.isEnabled = true
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                view.isEnabled = true
                            }
                        })
        )
    }

    companion object {

        fun newInstance(): SingInFragment {
            return SingInFragment()
        }
    }
}