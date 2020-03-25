package com.project.scratchstudio.kith_andoid.ui.neweditboard

import android.Manifest
import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.Service.PhotoService
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.databinding.FragmentNewAnnouncementBinding
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.board.Board
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.io.FileNotFoundException
import java.io.InputStream

class NewEditBoardFragment : BaseFragment() {
    private var binding: FragmentNewAnnouncementBinding? = null
    private var boardPresenter: BoardPresenter? = null

    private var bundle: Bundle? = null
    private var photo: Bitmap? = null
    private var board: Board? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bundle = arguments
        binding = FragmentNewAnnouncementBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        verifyStoragePermissions(activity)
        boardPresenter = BoardPresenter(context!!)
        binding!!.costText.addTextChangedListener(CurrencyTextWatcher(binding!!.costText))
        setButtonsListener()

        if (bundle!!.containsKey("is_edit") && bundle!!.getBoolean("is_edit")) {
            board = bundle!!.getSerializable("board") as Board
            fillFields()
        }
    }

    private fun fillFields() {
        binding!!.titleText.setText(board!!.title)
        binding!!.changeDescription.setText(board!!.description)
        if (board!!.url != "" && board!!.url != "null" && board!!.url != "") {
            Picasso.with(activity).load(board!!.url.replace("@[0-9]*".toRegex(), ""))
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding!!.newPhoto)
            binding!!.newPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        binding!!.costText.setText(board!!.cost)
    }

    private fun setButtonsListener() {
        binding!!.cancel.setOnClickListener { this.onClickClose(it) }
        binding!!.loadPhoto.setOnClickListener { this.onClickLoadPhoto(it) }
        binding!!.done.setOnClickListener { this.onClickDone(it) }
    }

    override fun onBackPressed(): Boolean {
        if (bundle!!.containsKey("is_edit") && bundle!!.getBoolean("is_edit")) {
            editBoard()
        }
        hideKeyboard(activity!!)
        return super.onBackPressed()
    }

    fun onClickDoneClose() {
        (activity as HomeActivity).changedBoardPhoto()
        onClickClose(null)
    }

    private fun onClickDone(view: View) {
        view.isEnabled = false
        hideKeyboard(activity!!)
        if (binding!!.titleText.text == null || binding!!.titleText.text.toString() == "" || binding!!.changeDescription.text == null
                || binding!!.changeDescription.text.toString() == "") {
            Toast.makeText(context, "Не заполнены все поля обьявления", Toast.LENGTH_SHORT).show()
        } else {
            if (board == null) {
                val newBoard = createBoard()
                boardPresenter!!.createBoard(object : BoardPresenter.BoardCallback {
                    override fun onSuccess(baseResponse: BaseResponse) {
                        if (baseResponse.status) {
                            Toast.makeText(context, "Объявление создано", Toast.LENGTH_SHORT).show()
                            (activity as HomeActivity).updateBoards()
                            onClickClose(null)
                        } else {
                            view.isEnabled = true
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        view.isEnabled = true
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, newBoard, photo)
            } else {
                boardPresenter!!.editBoard(object : BoardPresenter.BoardCallback {
                    override fun onSuccess(baseResponse: BaseResponse) {
                        if (baseResponse.status) {
                            Toast.makeText(context, "Объявление изменено", Toast.LENGTH_SHORT).show()
//                            (activity as HomeActivity).updateBoards()
                            onClickClose(null)
                        } else {
                            view.isEnabled = true
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        view.isEnabled = true
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, board!!, photo)
            }
        }
    }

    fun onClickClose(view: View?) {
        if (bundle!!.containsKey("is_edit") && bundle!!.getBoolean("is_edit")) {
            editBoard()
        }
        hideKeyboard(activity!!)
        (activity as HomeActivity).backFragment()
    }

    private fun editBoard() {
        board!!.title = binding!!.titleText.text.toString()
        board!!.description = binding!!.changeDescription.text.toString()
        board!!.cost = binding!!.costText.text!!.toString().replace(" \u20BD".toRegex(), "")
    }

    private fun createBoard(): Board {
        val info = Board()
        info.title = binding!!.titleText.text.toString()
        info.description = binding!!.changeDescription.text.toString()
        info.organizerId = HomeActivity.mainUser.id
        info.cost = binding!!.costText.text!!.toString().replace(" \u20BD".toRegex(), "")

        return info
    }

    private fun onClickLoadPhoto(view: View) {
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2000)
        } else {
            startGallery(view)
        }
    }

    private fun startGallery(view: View) {
        if (SystemClock.elapsedRealtime() - buttonCount < 1000) {
            return
        }

        view.isEnabled = false
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val load = activity!!.findViewById<Button>(R.id.loadPhoto)
        if (requestCode == 0 && resultCode == RESULT_OK && intent != null && intent.data != null) {
            val imageUri = intent.data
            val imageStream: InputStream?
            val photoService = PhotoService(activity!!)
            try {
                imageStream = activity!!.contentResolver.openInputStream(imageUri!!)
                photo = BitmapFactory.decodeStream(imageStream)
                photo = photoService.changePhoto(photo!!, imageUri)
                photo = photoService.resizeBitmap(photo!!, binding!!.newPhoto.width, binding!!.newPhoto.height)
                photo = photoService.compressPhoto(photo!!)
                binding!!.newPhoto.setImageBitmap(photo)
                binding!!.newPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            load.isEnabled = true
        } else if (requestCode == 0) {
            load.isEnabled = true
        }
    }

    companion object {

        private val REQUEST_EXTERNAL_STORAGE = 1
        private val buttonCount: Long = 0
        private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        private fun verifyStoragePermissions(activity: Activity?) {
            val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                )
            }
        }

        fun newInstance(bundle: Bundle): NewEditBoardFragment {
            val fragment = NewEditBoardFragment()
            fragment.arguments = bundle
            return fragment
        }

        private fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
