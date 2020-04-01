package com.project.scratchstudio.kith_andoid.ui.home_package.UserProfile

import android.accounts.NetworkErrorException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.project.scratchstudio.kith_andoid.activities.EditActivity
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.databinding.FragmentProfileBinding
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.UserApi
import com.project.scratchstudio.kith_andoid.network.model.referral.ReferralResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.network.model.user.UserResponse
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.Locale

class ProfileFragment : BaseFragment() {

    private var anotherUser: Boolean = false
    private var user: User? = null
    private var userApi: UserApi? = null
    private val disposable = CompositeDisposable()
    private var share: View? = null
    private var binding: FragmentProfileBinding? = null

    private var userPresenter: UserPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bundle = arguments
        user = bundle!!.getSerializable("user") as User
        anotherUser = bundle.getBoolean("another_user")
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        userApi = ApiClient.getClient(context!!).create<UserApi>(UserApi::class.java)

        if (anotherUser) {
            binding!!.edit.visibility = View.GONE
            binding!!.exit.visibility = View.GONE

            binding!!.myShare.visibility = View.GONE
            binding!!.share.visibility = View.VISIBLE
        }

        fullField()
        initButtonListener()
    }

    private fun fullField() {

        if (user!!.photo != "") {
            Picasso.with(context).load(user!!.photo.replace("@[0-9]*".toRegex(), ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding!!.photo)
        }

        binding!!.name.text = user!!.firstName
        binding!!.surname.text = user!!.lastName
        binding!!.phone.text = user!!.phone
        if (user!!.middleName != "" && user!!.middleName != "" && user!!.middleName.toLowerCase(Locale.getDefault()) != "null") {
            binding!!.middlename.text = user!!.middleName
        } else {
            binding!!.middlename.text = "-"
        }
        binding!!.position.text = user!!.position
        binding!!.usersCount.text = user!!.usersCount.toString()
        if (user!!.description == "" || user!!.description == "null" || user!!.description == "") {
            binding!!.description.text = "-"
        } else {
            binding!!.description.text = user!!.description
        }

        if (user!!.email == "" || user!!.email == "null" || user!!.email == "") {
            binding!!.email.text = "-"
        } else {
            binding!!.email.text = user!!.email
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            share!!.isEnabled = true
        }
        if (requestCode == 1) {
            if (data != null && resultCode == 0) {
                userPresenter!!.getUser(object : UserPresenter.GetUserCallback {
                    override fun onSuccess(userResponse: UserResponse) {
                        if (userResponse.user.photo != "") {
                            HomeActivity.mainUser.photo = userResponse.user.photo
                            Log.i("Profile", userResponse.user.photo)
                        } else {
                            Log.i("Profile", "null photo")
                        }
                        refreshUser(userResponse.user)
                        Toast.makeText(context, "Данные обновлены", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, HomeActivity.mainUser.id)
            }
        }
    }

    private fun refreshUser(refUser: User) {
        if (refUser.photo != "") {
            Picasso.with(context).load(refUser.photo.replace("@[0-9]*".toRegex(), ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding!!.photo)
        }
        binding!!.name.text = refUser.firstName
        binding!!.surname.text = refUser.lastName
        binding!!.phone.text = refUser.phone
        if (refUser.middleName != "" && refUser.middleName != "" && refUser.middleName.toLowerCase(Locale.getDefault()) != "null") {
            binding!!.middlename.text = refUser.middleName
        } else {
            binding!!.middlename.text = "-"
        }
        binding!!.position.text = refUser.position
        binding!!.usersCount.text = refUser.usersCount.toString()
        if (refUser.description == "" || refUser.description == "" || refUser.description.toLowerCase(Locale.getDefault()) == "null") {
            binding!!.description.text = "-"
        } else {
            binding!!.description.text = refUser.description
        }
        if (refUser.email == "" || refUser.email == "null" || refUser.email == "") {
            binding!!.email.text = "-"
        } else {
            binding!!.email.text = refUser.email
        }
    }

    private fun initButtonListener() {
        binding!!.back.setOnClickListener { this.onClickBack(it) }
        binding!!.exit.setOnClickListener { this.onClickExitButton(it) }
        binding!!.edit.setOnClickListener { this.onClickEditButton(it) }
        binding!!.share.setOnClickListener { this.onClickContactShare(it) }
        binding!!.myShare.setOnClickListener { this.onClickContactShare(it) }
        binding!!.phone.setOnClickListener { this.onClickCallPhone(it) }
    }

    private fun onClickBack(view: View) {
        (activity as HomeActivity).backFragment()
    }

    private fun onClickExitButton(view: View) {
        view.isEnabled = false
        (activity as HomeActivity).exit()
    }

    private fun onClickEditButton(view: View) {
        view.isEnabled = false
        val intent = Intent(activity, EditActivity::class.java)
        startActivityForResult(intent, 1)
        view.isEnabled = true
    }

    private fun onClickCallPhone(view: View) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + user!!.phone)
        startActivity(intent)
    }

    private fun onClickContactShare(view: View) {
        view.isEnabled = false
        share = view
        disposable.add(
                userApi!!.getReferralCode(HomeActivity.mainUser.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<ReferralResponse>() {
                            override fun onSuccess(response: ReferralResponse) {
                                Log.i("REf", response.status.toString() + " " + response.referral)
                                val result = ("ФИО: " + binding!!.surname.text + " " + binding!!.name.text + " " + binding!!.middlename
                                        .text + "\n"
                                        + (if (binding!!.phone.text.length > 1) "Телефон: " + binding!!.phone.text + "\n" else "")
                                        + (if (binding!!.email.text.length > 1) "Эл.почта: " + binding!!.email.text + "\n" else "")
                                        + (if (user!!.position.length > 1) "Сфера деятельности: " + user!!.position + "\n" else "")
                                        + "Приложение: " + Const.APP_URL + "\n"
                                        + "Реф.код для регистрации: " + response.referral + "\n"
                                        + getString(R.string.signature))

                                shareDate(result)
                            }

                            override fun onError(e: Throwable) {
                                val result = ("ФИО: " + binding!!.surname.text + " " + binding!!.name.text + " " + binding!!.middlename
                                        .text + "\n"
                                        + "Телефон: " + binding!!.phone.text + "\n"
                                        + "Эл.почта: " + binding!!.email.text + "\n"
                                        + "Сфера деятельности: " + user!!.position + "\n"
                                        + "Приложение: " + Const.APP_URL + "\n"
                                        + getString(R.string.signature))

                                shareDate(result)
                            }
                        })
        )
    }

    private fun shareDate(share: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, share)
        sendIntent.type = "text/plain"
        startActivityForResult(sendIntent, 0)
    }

    override fun onBackPressed(): Boolean {
        return (activity as HomeActivity).backFragment()
    }

    companion object {

        fun newInstance(bundle: Bundle): ProfileFragment {
            val fragment = ProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}
