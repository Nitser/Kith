package com.project.scratchstudio.kith_andoid.ui.home_package.user_profile

import android.accounts.NetworkErrorException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.app.UserType
import com.project.scratchstudio.kith_andoid.databinding.FragmentProfileBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.ui.home_package.user_edit_profile.EditProfileFragment
import com.project.scratchstudio.kith_andoid.view_model.CurrentUserViewModel
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.util.Locale

class ProfileFragment : BaseFragment() {

    private var share: View? = null
    private var binding: FragmentProfileBinding? = null
    private var userPresenter: UserPresenter? = null

    private val currentUserViewModel: CurrentUserViewModel by activityViewModels()
    private val mainUserViewModel: MainUserViewModel by activityViewModels()
    private lateinit var user: UserModelView
    private lateinit var userType: UserType

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile_exit -> {
                HomeActivity.exit(activity!!)
                return true
            }
            R.id.menu_profile_edit -> {
                activity!!.findNavController(R.id.nav_host_fragment_home)
                        .navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        userType = ProfileFragmentArgs.fromBundle(arguments!!).userType
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        user = if(userType == UserType.MAIN_USER) {
            mainUserViewModel.getMainUser().value!!
        } else {
            currentUserViewModel.getCurrentUser().value!!
        }
        fullField()
        initButtonListener()
    }

    private fun fullField() {

        if (user.photo != null && user.photo != "") {
            Picasso.with(context).load(user.photo!!.replace("@[0-9]*".toRegex(), ""))
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding!!.photo)
        }

        binding!!.name.text = user.firstName
        binding!!.surname.text = user.lastName
        binding!!.phone.text = user.phone
        if (user.middleName != "" && user.middleName != "" && user.middleName.toLowerCase(Locale.getDefault()) != "null") {
            binding!!.middlename.text = user.middleName
        } else {
            binding!!.middlename.text = "-"
        }
        binding!!.position.text = user.position
        binding!!.usersCount.text = user.usersCount.toString()
        if (user.description == "" || user.description == "null" || user.description == "") {
            binding!!.description.text = "-"
        } else {
            binding!!.description.text = user.description
        }

        if (user.email == "" || user.email == "null" || user.email == "") {
            binding!!.email.text = "-"
        } else {
            binding!!.email.text = user.email
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            share!!.isEnabled = true
        }
        if (requestCode == 1) {
            if (data != null && resultCode == 0) {
                userPresenter!!.getUser(object : UserPresenter.GetUserCallback {
                    override fun onSuccess(userResponse: User) {
                        if (userResponse.photo != "") {
                            user.photo = userResponse.photo
                            Log.i("Profile", userResponse.photo)
                        } else {
                            Log.i("Profile", "null photo")
                        }
                        refreshUser(userResponse)
                        Toast.makeText(context, "Данные обновлены", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, user.id)
            }
        }
    }

    private fun refreshUser(refUser: User) {
        if (refUser.photo != "" && refUser.photo != null) {
            Picasso.with(context).load(refUser.photo!!.replace("@[0-9]*".toRegex(), ""))
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
//        binding!!.share.setOnClickListener { this.onClickContactShare(it) }
        binding!!.myShare.setOnClickListener { this.onClickContactShare(it) }
        binding!!.phone.setOnClickListener { this.onClickCallPhone(it) }
    }

    private fun onClickBack(view: View) {
        (activity as HomeActivity).backFragment()
    }

    private fun onClickEditButton(view: View) {
        view.isEnabled = false
        val intent = Intent(activity, EditProfileFragment::class.java)
        startActivityForResult(intent, 1)
        view.isEnabled = true
    }

    private fun onClickCallPhone(view: View) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + user.phone)
        startActivity(intent)
    }

    private fun onClickContactShare(view: View) {
        view.isEnabled = false
        share = view
        val result = ("ФИО: " + binding!!.surname.text + " " + binding!!.name.text + " " + binding!!.middlename
                .text + "\n"
                + (if (binding!!.phone.text.length > 1) "Телефон: " + binding!!.phone.text + "\n" else "")
                + (if (binding!!.email.text.length > 1) "Эл.почта: " + binding!!.email.text + "\n" else "")
                + (if (user.position.length > 1) "Сфера деятельности: " + user.position + "\n" else "")
                + "Приложение: " + Const.APP_URL + "\n"
                + "Реф.код для регистрации: " + user.referralCode + "\n"
                + getString(R.string.signature))
        shareDate(result)
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
