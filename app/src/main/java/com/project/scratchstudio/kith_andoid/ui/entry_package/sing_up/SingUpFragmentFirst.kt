package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpFirstBinding
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.utils.PhotoService
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class SingUpFragmentFirst : BaseFragment() {

    private lateinit var binding: FragmentSingUpFirstBinding
    private var currentPhoto: PhotoModelView? = null
    lateinit var editTextBehavior: EditTextBehavior
    private lateinit var user: UserModelView
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpFirstBinding.inflate(layoutInflater)
        initButtons()
        user = SingUpFragmentFirstArgs.fromBundle(arguments!!).user
        editTextBehavior = EditTextBehavior(context!!)
        return binding.root
    }

    private fun initButtons() {
        binding.photo.findViewById<ImageButton>(R.id.custom_circle_image_view_add_photo).setOnClickListener(this::chooseImageButton)
        binding.next.setOnClickListener(this::onClickNext)
    }

    private fun onClickNext(view: View) {
        if (checkImportantFields()) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            val user = parseUser()
            view.findNavController().navigate(SingUpFragmentFirstDirections.actionSingUpFragmentToSingUpFragmentSecond(user))
        }
    }

    private fun checkImportantFields(): Boolean {
        return editTextBehavior.notEmptyField(binding.firstName).and(editTextBehavior.notEmptyField(binding.lastName))
    }

    private fun parseUser(): UserModelView {
        with(user) {
            firstName = binding.firstName.text.toString()
            lastName = binding.lastName.text.toString()
            middleName = binding.middleName.text.toString()
            position = binding.organisation.text.toString()
            if(currentPhoto != null) {
                photoBitmap = currentPhoto!!.photoBitmap
                photoFile = currentPhoto!!.phoneStorageFile
            }
        }
        return user
    }

    override fun onResume() {
        super.onResume()
        if (currentPhoto != null && currentPhoto!!.photoBitmap != null) {
            binding.photo.findViewById<CircleImageView>(R.id.custom_circle_image_view_photo).setImageBitmap(currentPhoto!!.photoBitmap)
        }
    }

    private fun chooseImageButton(view: View) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_REQUEST_CODE)
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                choosePhoto()
            }
        } else {
            choosePhoto()
        }
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhoto()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && intent != null && intent.data != null) {
            val imageUri = intent.data
            user.photoPath = imageUri!!.path.toString()
            user.photoFile = File(imageUri.path.toString())

            val imageStream: InputStream?
            val photoService = PhotoService(context!!)
            try {
                imageStream = activity?.contentResolver?.openInputStream(imageUri)
                var bitmap = BitmapFactory.decodeStream(imageStream)
                if (bitmap != null) {
                    bitmap = photoService.changePhoto(bitmap, imageUri)
                    bitmap = photoService.compressPhoto(bitmap)
                    binding.photo.findViewById<CircleImageView>(R.id.custom_circle_image_view_photo).setImageBitmap(bitmap)
                    currentPhoto = photoService.fullPreparingPhoto(bitmap, imageUri)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        fun newInstance(): SingUpFragmentFirst {
            return SingUpFragmentFirst()
        }
    }
}