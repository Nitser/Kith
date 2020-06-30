package com.project.scratchstudio.kith_andoid.ui.home_package.user_edit_profile

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.databinding.FragmentProfileEditBinding
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.utils.PhotoService
import com.project.scratchstudio.kith_andoid.utils.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.view_model.MainUserViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.InputStream

class EditProfileFragment : BaseFragment() {

    private lateinit var userPresenter: UserPresenter
    private lateinit var binding: FragmentProfileEditBinding
    private val mainUserViewModel: MainUserViewModel by activityViewModels()

    private var newPhoto: PhotoModelView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(requireContext())
        init()
        mainUserViewModel.getMainUser().observe(viewLifecycleOwner, Observer<UserModelView> {
            binding.profileEditPassword.text = String(CharArray(it.password.length)).replace("\u0000", "*")
        })
    }

    private fun init() {
        binding.profileEditButtonAddPhoto.setOnClickListener { chooseImageButton() }
        binding.profileEditButtonChangePassword.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_home)
                    .navigate(EditProfileFragmentDirections.actionEditProfileFragmentToChangePasswordFragment())
        }
        binding.profileEditButtonSave.setOnClickListener { onClickRefreshButton() }

        with(mainUserViewModel.getMainUser().value!!) {
            if (photo != null)
                Picasso.with(context!!).load(Const.BASE_URL + photo)
                        .placeholder(R.mipmap.person)
                        .error(R.mipmap.person)
                        .transform(PicassoCircleTransformation())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(binding.profileEditUserPhoto)

            binding.profileEditFieldFirstname.setText(firstName)
            binding.profileEditFieldLastname.setText(lastName)
            binding.profileEditFieldMiddlename.setText(middleName)
            binding.profileEditFieldPosition.setText(position)
            binding.profileEditFieldEmail.setText(email)
            binding.profileEditFieldPhone.setText(phone)
            binding.profileEditFieldDescription.setText(description)
            binding.profileEditPassword.text = String(CharArray(password.length)).replace("\u0000", "*")
        }
    }

    private fun getUserNewInformation(): UserModelView? {
        if (binding.profileEditFieldFirstname.text.toString().isEmpty()
                || binding.profileEditFieldLastname.text.toString().isEmpty()
                || binding.profileEditFieldEmail.text.toString().isEmpty()
                || binding.profileEditFieldPhone.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Не все поля заполнены", Toast.LENGTH_SHORT).show()
            return null
        } else {
            val user = mainUserViewModel.getMainUser().value!!
            with(user) {
                firstName = binding.profileEditFieldFirstname.text.toString()
                lastName = binding.profileEditFieldLastname.text.toString()
                middleName = binding.profileEditFieldMiddlename.text.toString()
                position = binding.profileEditFieldPosition.text.toString()
                email = binding.profileEditFieldEmail.text.toString()
                phone = binding.profileEditFieldPhone.text.toString()
                description = binding.profileEditFieldDescription.text.toString()
                mainUserViewModel.setMainUser(user)
                if (newPhoto != null) {
                    photoBitmap = newPhoto!!.photoBitmap
                    photoFile = newPhoto!!.phoneStorageFile
                }
            }
            return user
        }
    }

    private fun chooseImageButton() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 0)
    }

    private fun onClickRefreshButton() {
        val refreshUser = getUserNewInformation()
        if (refreshUser != null) {
            userPresenter.editUser(object : UserPresenter.BaseCallback {
                override fun onSuccess(baseResponse: BaseResponse) {
                    mainUserViewModel.setMainUser(refreshUser)
                    requireActivity().findNavController(R.id.nav_host_fragment_home).popBackStack()
                }

                override fun onError(networkError: NetworkErrorException) {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
            }, refreshUser, newPhoto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && intent != null && intent.data != null) {
            val imageUri = intent.data
            val imageStream: InputStream?
            val photoService = PhotoService(requireContext())
            imageStream = requireActivity().contentResolver.openInputStream(imageUri!!)
            var bitmap = BitmapFactory.decodeStream(imageStream)

            newPhoto = photoService.fullPreparingPhoto(bitmap, imageUri)
            if (bitmap != null) {
                bitmap = photoService.changePhoto(bitmap, imageUri)
                bitmap = photoService.changePhoto(bitmap, imageUri)
                bitmap = photoService.compressPhoto(bitmap)
                binding.profileEditUserPhoto.setImageBitmap(bitmap)
                newPhoto!!.photoBitmap = bitmap
            }
        }
    }


}
