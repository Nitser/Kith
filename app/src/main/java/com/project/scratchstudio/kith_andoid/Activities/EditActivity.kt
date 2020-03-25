package com.project.scratchstudio.kith_andoid.Activities

import android.accounts.NetworkErrorException
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.Service.PhotoService
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.databinding.ActivityEditProfileBinding
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.ArrayList
import java.util.Objects

class EditActivity : AppCompatActivity() {
    private var isChanged = false
    private var currentBitmap: Bitmap? = null
    private val fields = ArrayList<EditText>()
    private var context: Context? = null

    private var userPresenter: UserPresenter? = null
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        init()
        userPresenter = UserPresenter(this)
        context = this
    }

    private fun init() {
        val mainUser = HomeActivity.mainUser

        fields.add(binding.firstName)
        fields.add(binding.lastName)
        fields.add(binding.middleName)
        fields.add(binding.position)
        fields.add(binding.email)
        fields.add(binding.phone)
        fields.add(binding.description)
        Picasso.with(this).load(mainUser.photo.replace("@[0-9]*".toRegex(), ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(binding.portfolio)

        binding.firstName.setText(mainUser.firstName)
        binding.lastName.setText(mainUser.lastName)
        if (mainUser.middleName != "" && mainUser.middleName.toLowerCase() != "null") {
            binding.middleName.setText(mainUser.middleName)
        }
        binding.password.text = mainUser.position
        if (mainUser.email != "" && mainUser.email.toLowerCase() != "null") {
            binding.email.setText(mainUser.email)
        }
        binding.phone.setText(mainUser.phone)
        if (mainUser.description != "" && mainUser.description.toLowerCase() != "null") {
            binding.description.setText(mainUser.description)
        }
        binding.password.text = String(CharArray(mainUser.password!!.length)).replace("\u0000", "*")
        binding.position.setText(mainUser.position)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun makeRefreshUser(): User {
        val user = User()
        try {
            user.id = HomeActivity.mainUser.id
            user.token = HomeActivity.mainUser.token
            user.firstName = Objects.requireNonNull<Editable>(fields[0].text).toString()
            user.lastName = Objects.requireNonNull<Editable>(fields[1].text).toString()
            user.middleName = Objects.requireNonNull<Editable>(fields[2].text).toString().replace("^null$".toRegex(), "")
            user.position = Objects.requireNonNull<Editable>(fields[3].text).toString()
            user.email = Objects.requireNonNull<Editable>(fields[4].text).toString().replace("^null$".toRegex(), "")
            user.phone = Objects.requireNonNull<Editable>(fields[5].text).toString()
            user.description = Objects.requireNonNull<Editable>(fields[6].text).toString().replace("^null$".toRegex(), "")

        } catch (ignored: NullPointerException) {
        }

        return user
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && intent != null && intent.data != null) {
            val imageUri = intent.data
            val imageStream: InputStream?
            val photoService = PhotoService(this)
            try {
                imageStream = contentResolver.openInputStream(imageUri!!)
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
        } else if (requestCode == 3 && resultCode == DATA_CHANGED && intent != null) {
            binding.password.text = String(CharArray(HomeActivity.mainUser.password.length)).replace("\u0000", "*")
        }
    }

    fun chooseImageButton(view: View) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return
        }
        view.isEnabled = false
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 0)
    }

    fun onClickCancelButton(view: View) {
        finish()
    }

    fun onClickRefreshButton(view: View) {
        view.isEnabled = false
        val refreshUser = makeRefreshUser()
        userPresenter!!.editUser(object : UserPresenter.EditUserCallback {
            override fun onSuccess(baseResponse: BaseResponse) {
                if (baseResponse.status) {
                    isChanged = true
                    finishEdit()
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

    fun finishEdit() {
        val intent = Intent()
        if (isChanged) {
            setResult(DATA_CHANGED, intent)
        } else {
            setResult(DATA_NOT_CHANGED, intent)
        }
        finish()
    }

    fun onClickChangePassword(view: View) {
        view.isEnabled = false
        val intent = Intent(this@EditActivity, ChangePasswordActivity::class.java)
        startActivityForResult(intent, 3)
        view.isEnabled = true
    }

    companion object {

        private val DATA_CHANGED = 0
        private val DATA_NOT_CHANGED = 1
        private val buttonCount: Long = 0
    }
}
