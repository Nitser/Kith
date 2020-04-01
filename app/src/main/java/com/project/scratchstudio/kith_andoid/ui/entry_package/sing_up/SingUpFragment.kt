package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.project.scratchstudio.kith_andoid.activities.AgreementActivity
import com.project.scratchstudio.kith_andoid.activities.EntryActivity
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.activities.TermActivity
import com.project.scratchstudio.kith_andoid.custom_views.CustomFontEditText
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpBinding
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse
import com.project.scratchstudio.kith_andoid.service.PhotoService
import com.project.scratchstudio.kith_andoid.ui.entry_package.sms_check.CheckSmsFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.HashMap
import java.util.regex.Pattern

class SingUpFragment : BaseFragment() {

    private lateinit var binding: FragmentSingUpBinding
    private val requiredFields = HashMap<String, CustomFontEditText>()
    private lateinit var entryApi: EntryApi
    private val disposable = CompositeDisposable()
    private lateinit var photoButton: ImageButton
    private lateinit var checkInButton: Button
    private var currentBitmap: Bitmap? = null
    private var status = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpBinding.inflate(layoutInflater)
        entryApi = ApiClient.getClient(context!!).create<EntryApi>(EntryApi::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextInitialized()
        initButtons()
        customTextView()
    }

    private fun initButtons() {
        binding.back.setOnClickListener(this::onClickBack)
        binding.buttonPhoto.setOnClickListener(this::chooseImageButton)
        binding.singUp.setOnClickListener(this::onClickCheckInButton)
    }

    private fun customTextView() {
        val spanTxt = SpannableStringBuilder("Я согласен с ")
        spanTxt.append("пользовательским соглашением")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(context, AgreementActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }, spanTxt.length - "пользовательским соглашением".length, spanTxt.length, 0)
        spanTxt.append(" и ")
        spanTxt.append("политикой конфиденциальности")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(context, TermActivity::class.java)
                startActivityForResult(intent, 2)
            }
        }, spanTxt.length - "политикой конфиденциальности".length, spanTxt.length, 0)
        binding.agree.movementMethod = LinkMovementMethod.getInstance()
        binding.agree.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun editTextInitialized() {
        editTextListener(binding.lastName)
        editTextListener(binding.firstName)
        editTextListener(binding.middleName)
        editTextListener(binding.login)
        editTextListener(binding.password)
        editTextListener(binding.phone)
        editTextListener(binding.position)
        editTextListener(binding.refCode)
        editTextListener(binding.description)

        //        CustomFontEditText phone = findViewById(R.id.editText4);
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //            phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("RU"));
        //        }
    }

    private fun editTextListener(field: EditText) {
        field.setOnFocusChangeListener { view, b ->
            if (b)
                view.setBackgroundResource(R.drawable.entri_field_focus_check_in)
            else
                view.setBackgroundResource(R.drawable.entry_field_check_in)
        }
    }

    fun onClickCheckInButton(view: View) {
        checkInButton = view as Button
        view.isEnabled = false
        status = true

        disposable.add(
                entryApi.checkReferralCode(binding.refCode.text.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<EntryResponse>() {
                            override fun onSuccess(t: EntryResponse) {
                                if (t.status) {
                                    checkFields()
                                    val userId = t.userId
                                    checkIn(userId)
                                } else {
                                    checkFields()
                                    binding.refCode.setBackgroundResource(R.drawable.entri_field_error_check_in)
                                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                    view.isEnabled = true
                                }
                            }

                            override fun onError(e: Throwable) {
                                checkFields()
                                binding.refCode.setBackgroundResource(R.drawable.entri_field_error_check_in)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                view.isEnabled = true
                            }
                        })
        )
    }

    fun checkFields() {
        val p = Pattern.compile("^(\\+7|8){1}\\s?(\\d){3}\\s?(\\d){3}\\-?(\\d){2}\\-?(\\d){2}")

        for ((key, value) in requiredFields) {
            if (key != "user_middlename" && key != "user_description") {
                val m = p.matcher(value.text)
                if (value.text!!.toString().trim { it <= ' ' } == "") {
                    value.setBackgroundResource(R.drawable.entri_field_error_check_in)
                    status = false
                }
                if (key == "user_password" && value.length() < 6) {
                    value.setBackgroundResource(R.drawable.entri_field_error_check_in)
                    status = false
                    Toast.makeText(context, "Пароль должен быть не короче 6 символов ", Toast.LENGTH_LONG).show()
                }
                if (key == "user_phone" && !m.find()) {
                    //                    Log.i("PHONE: ", field.getValue().getText().toString());
                    value.setBackgroundResource(R.drawable.entri_field_error_check_in)
                    status = false
                    Toast.makeText(context, "Неверный формат телефона", Toast.LENGTH_LONG).show()
                }
            }
        }
        if (!binding.checkbox.isChecked) {
            status = false
            binding.checkbox.buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.colorError))
            checkInButton.isEnabled = true
        }
    }

    fun checkIn(parent_id: Int) {
        if (status) {
            checkInButton.isEnabled = false
            val photoService = PhotoService(context!!)
            val res = photoService.base64Photo(currentBitmap)

            disposable.add(
                    entryApi.singUp(binding.firstName.text.toString(), binding.lastName.text.toString(), binding.middleName.text.toString(),
                            binding.phone.text.toString(), binding.login.text.toString(), binding.password.text.toString(),
                            parent_id, binding.position.text.toString(), binding.description.text.toString(), res)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableSingleObserver<EntryResponse>() {
                                override fun onSuccess(response: EntryResponse) {
                                    if (response.status) {
                                        HomeActivity.createMainUser()
                                        HomeActivity.mainUser.id = response.userId
                                        HomeActivity.mainUser.login = binding.login.text.toString()
                                        HomeActivity.mainUser.image = currentBitmap
                                        HomeActivity.mainUser.password = binding.password.text.toString()
                                        (activity as EntryActivity).replaceFragment(CheckSmsFragment.newInstance(), FragmentType.SMS.name)
                                    } else {
                                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                        checkInButton.isEnabled = true
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                    checkInButton.isEnabled = true
                                }
                            })
            )
        } else {
            binding.scroll.fullScroll(ScrollView.FOCUS_UP)
            checkInButton.isEnabled = true
        }
    }

    fun chooseImageButton(view: View) {
        view.isEnabled = false
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
                    binding.photo.setImageBitmap(currentBitmap)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            photoButton.isEnabled = true
        } else if (requestCode == 0) {
            photoButton.isEnabled = true
        }
    }

    fun onCheckButton() {
        binding.checkbox.buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.colorDark))
    }

    fun onClickBack(view: View) {
        (activity as EntryActivity).backFragment()
    }

    companion object {

        fun newInstance(): SingUpFragment {
            return SingUpFragment()
        }
    }
}