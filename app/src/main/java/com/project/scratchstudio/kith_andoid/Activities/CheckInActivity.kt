package com.project.scratchstudio.kith_andoid.Activities

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontEditText
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.Service.PhotoService
import com.project.scratchstudio.kith_andoid.databinding.ActivityCheckInBinding
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.EntryApi
import com.project.scratchstudio.kith_andoid.network.model.entry.EntryResponse
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.HashMap
import java.util.regex.Pattern

class CheckInActivity : AppCompatActivity() {

    private var buttonCount: Long = 0
    private val requiredFields = HashMap<String, CustomFontEditText>()
    private lateinit var entryApi: EntryApi
    private val disposable = CompositeDisposable()
    private lateinit var binding: ActivityCheckInBinding

    private lateinit var photoButton: ImageButton
    private lateinit var checkInButton: Button
    private var currentBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryApi = ApiClient.getClient(applicationContext).create<EntryApi>(EntryApi::class.java)
        binding = ActivityCheckInBinding.inflate(layoutInflater)
//        setContentView(R.layout.activity_check_in)
        setContentView(binding.root)
        editTextInitialized()

        customTextView()
//        button.typeface = Typeface.createFromAsset(assets, "fonts/intro_regular.ttf")
    }

    private fun customTextView() {
        val spanTxt = SpannableStringBuilder("Я согласен с ")
        spanTxt.append("пользовательским соглашением")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
                buttonCount = SystemClock.elapsedRealtime()

                val intent = Intent(this@CheckInActivity, AgreementActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }, spanTxt.length - "пользовательским соглашением".length, spanTxt.length, 0)
        spanTxt.append(" и ")
        spanTxt.append("политикой конфиденциальности")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
                buttonCount = SystemClock.elapsedRealtime()

                val intent = Intent(this@CheckInActivity, TermActivity::class.java)
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
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
        buttonCount = SystemClock.elapsedRealtime()

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
                                    Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                    view.isEnabled = true
                                }
                            }

                            override fun onError(e: Throwable) {
                                checkFields()
                                binding.refCode.setBackgroundResource(R.drawable.entri_field_error_check_in)
                                Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Пароль должен быть не короче 6 символов ", Toast.LENGTH_LONG).show()
                }
                if (key == "user_phone" && !m.find()) {
                    //                    Log.i("PHONE: ", field.getValue().getText().toString());
                    value.setBackgroundResource(R.drawable.entri_field_error_check_in)
                    status = false
                    Toast.makeText(this, "Неверный формат телефона", Toast.LENGTH_LONG).show()
                }
            }
        }
        val check = findViewById<CheckBox>(R.id.checkbox)
        if (!check.isChecked) {
            status = false
            check.buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.colorError))
            checkInButton.isEnabled = true
        }
    }

    fun checkIn(parent_id: Int) {
        if (status) {
            checkInButton.isEnabled = false
            val photoService = PhotoService(this)
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
                                        val intent = Intent(applicationContext, SmsActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                        checkInButton.isEnabled = true
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Toast.makeText(applicationContext, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                    checkInButton.isEnabled = true
                                }
                            })
            )
        } else {
            val scrollView = findViewById<ScrollView>(R.id.scroll)
            scrollView.fullScroll(ScrollView.FOCUS_UP)
            checkInButton.isEnabled = true
        }
    }

    fun chooseImageButton(view: View) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
        buttonCount = SystemClock.elapsedRealtime()

        view.isEnabled = false

        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && intent != null && intent.data != null) {
            val image = findViewById<CircleImageView>(R.id.portfolio)
            val imageUri = intent.data
            val imageStream: InputStream?
            val photoService = PhotoService(this)
            try {
                imageStream = contentResolver.openInputStream(imageUri!!)
                currentBitmap = BitmapFactory.decodeStream(imageStream)
                if (currentBitmap != null) {
                    currentBitmap = photoService.changePhoto(currentBitmap!!, imageUri)
                    currentBitmap = photoService.compressPhoto(currentBitmap!!)
                    image.setImageBitmap(currentBitmap)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            photoButton.isEnabled = true
        } else if (requestCode == 0) {
            photoButton.isEnabled = true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this@CheckInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun onCheckButton() {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
        buttonCount = SystemClock.elapsedRealtime()

        val check = findViewById<CheckBox>(R.id.checkbox)
        check.buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.colorDark))
    }

    fun onClickBack(view: View) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) return
        buttonCount = SystemClock.elapsedRealtime()

        val intent = Intent(this@CheckInActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private var status = true
    }
}
