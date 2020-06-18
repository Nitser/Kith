package com.project.scratchstudio.kith_andoid.ui.home_package.user_edit_profile

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.databinding.FragmentProfileEditBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.service.PhotoService
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList
import java.util.Objects

class EditProfileFragment : BaseFragment() {
    private var isChanged = false
    private var currentBitmap: Bitmap? = null
    private val fields = ArrayList<EditText>()
    private var userPresenter: UserPresenter? = null
    private lateinit var binding: FragmentProfileEditBinding
    private val mainUserViewModel: MainUserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        init()
    }

    private fun init() {
        fields.add(binding.firstName)
        fields.add(binding.lastName)
        fields.add(binding.middleName)
        fields.add(binding.position)
        fields.add(binding.email)
        fields.add(binding.phone)
        fields.add(binding.description)

        with(mainUserViewModel.getMainUser().value!!) {
            if (photo != null)
                Picasso.with(context!!).load(photo!!.replace("@[0-9]*".toRegex(), ""))
                        .placeholder(R.mipmap.person)
                        .error(R.mipmap.person)
                        .transform(PicassoCircleTransformation())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(binding.portfolio)

            binding.firstName.setText(firstName)
            binding.lastName.setText(lastName)
            if (middleName != "" && middleName.toLowerCase() != "null") {
                binding.middleName.setText(middleName)
            }
            binding.password.text = position
            if (email != "" && email.toLowerCase() != "null") {
                binding.email.setText(email)
            }
            binding.phone.setText(phone)
            if (description != "" && description.toLowerCase() != "null") {
                binding.description.setText(description)
            }
            binding.password.text = String(CharArray(password!!.length)).replace("\u0000", "*")
            binding.position.setText(position)
        }
    }

    private fun makeRefreshUser(): UserModelView {
        val user = mainUserViewModel.getMainUser().value!!
        try {
            with(user) {
                firstName = Objects.requireNonNull<Editable>(fields[0].text).toString()
                lastName = Objects.requireNonNull<Editable>(fields[1].text).toString()
                middleName = Objects.requireNonNull<Editable>(fields[2].text).toString().replace("^null$".toRegex(), "")
                position = Objects.requireNonNull<Editable>(fields[3].text).toString()
                email = Objects.requireNonNull<Editable>(fields[4].text).toString().replace("^null$".toRegex(), "")
                phone = Objects.requireNonNull<Editable>(fields[5].text).toString()
                description = Objects.requireNonNull<Editable>(fields[6].text).toString().replace("^null$".toRegex(), "")
                mainUserViewModel.setMainUser(user)
            }
        } catch (ignored: NullPointerException) {
        }
        return user
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && intent != null && intent.data != null) {
            val imageUri = intent.data
            val imageStream: InputStream?
            val photoService = PhotoService(context!!)
            try {
                imageStream = activity!!.contentResolver.openInputStream(imageUri!!)
                currentBitmap = BitmapFactory.decodeStream(imageStream)
                if (currentBitmap != null) {
                    currentBitmap = photoService.changePhoto(currentBitmap!!, imageUri)
                    currentBitmap = photoService.changePhoto(currentBitmap!!, imageUri)
                    currentBitmap = photoService.compressPhoto(currentBitmap!!)
                    binding.portfolio.setImageBitmap(currentBitmap)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            binding.buttonPhoto.isEnabled = true
        } else if (requestCode == 0) {
            binding.buttonPhoto.isEnabled = true
        } else if (requestCode == 3 && intent != null) {
            binding.password.text = String(CharArray(mainUserViewModel.getMainUser().value!!.password.length)).replace("\u0000", "*")
        }
    }

    fun chooseImageButton(view: View) {
        view.isEnabled = false
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 0)
    }

    fun onClickRefreshButton(view: View) {
        view.isEnabled = false
        val refreshUser = makeRefreshUser()
        userPresenter!!.editUser(object : UserPresenter.BaseCallback {
            override fun onSuccess(baseResponse: BaseResponse) {
                if (baseResponse.status) {
                    isChanged = true
                } else {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    view.isEnabled = true
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                view.isEnabled = true
            }
        }, refreshUser, currentBitmap)
    }

    companion object {

        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
    }
}
