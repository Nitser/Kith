package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpFirstBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.service.PhotoService
import de.hdodenhof.circleimageview.CircleImageView
import java.io.FileNotFoundException
import java.io.InputStream

class SingUpFragmentFirst : BaseFragment() {

    private lateinit var binding: FragmentSingUpFirstBinding
    private var currentBitmap: Bitmap? = null
    lateinit var editTextBehavior: EditTextBehavior

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpFirstBinding.inflate(layoutInflater)
        initButtons()
        editTextBehavior = EditTextBehavior(context!!)
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.registration)
        return binding.root
    }

    private fun initButtons() {
        binding.photo.findViewById<ImageButton>(R.id.custom_circle_image_view_add_photo).setOnClickListener(this::chooseImageButton)
        binding.next.setOnClickListener(this::onClickNext)
    }

    private fun onClickNext(view: View) {
        if (checkImportantFields()) {
            val user = parseUser()
            view.findNavController().navigate(SingUpFragmentFirstDirections.actionSingUpFragmentToSingUpFragmentSecond(user))
        }
    }

    private fun checkImportantFields(): Boolean {
        return editTextBehavior.notEmptyField(binding.firstName).and(editTextBehavior.notEmptyField(binding.lastName))
    }

    private fun parseUser(): UserModelView {
        val user = UserModelView()
        with(user) {
            firstName = binding.firstName.text.toString()
            lastName = binding.lastName.text.toString()
            middleName = binding.middleName.text.toString()
            position = binding.organisation.text.toString()
        }
        return user
    }

    private fun chooseImageButton(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && intent != null && intent.data != null) {
            val imageUri = intent.data
            val imageStream: InputStream?
            val photoService = PhotoService(context!!)
            try {
                imageStream = activity?.contentResolver?.openInputStream(imageUri!!)
                currentBitmap = BitmapFactory.decodeStream(imageStream)
                if (currentBitmap != null) {
                    currentBitmap = photoService.changePhoto(currentBitmap!!, imageUri!!)
                    currentBitmap = photoService.compressPhoto(currentBitmap!!)
                    binding.photo.findViewById<CircleImageView>(R.id.custom_circle_image_view_photo).setImageBitmap(currentBitmap)
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