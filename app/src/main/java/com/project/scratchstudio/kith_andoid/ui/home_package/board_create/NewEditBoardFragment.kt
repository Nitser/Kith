package com.project.scratchstudio.kith_andoid.ui.home_package.board_create

import android.Manifest
import android.accounts.NetworkErrorException
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.UserType
import com.project.scratchstudio.kith_andoid.databinding.FragmentBoardCreateBinding
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.category.Category
import com.project.scratchstudio.kith_andoid.network.model.category.CategoryResponse
import com.project.scratchstudio.kith_andoid.service.PhotoService
import com.project.scratchstudio.kith_andoid.ui.home_package.board_info.ImageViewPageAdapter
import com.project.scratchstudio.kith_andoid.view_model.CurrentBoardViewModel
import java.io.File
import java.io.InputStream

class NewEditBoardFragment : BaseFragment() {
    private lateinit var binding: FragmentBoardCreateBinding
    private var boardPresenter: BoardPresenter? = null
    private lateinit var userType: UserType
    private val currentBoardViewModel: CurrentBoardViewModel by activityViewModels()
    private var categoriesList = ArrayList<Category>()
    private var categoryPosition: Int = 0
    private lateinit var adapter: ArrayAdapter<CharSequence>
    private lateinit var viewPagerAdapter: ImageViewPageAdapter

    private var newPhotos = ArrayList<File>()
    private var dotscount: Int = 0
    private var dots = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board_create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_board_create_done -> {
                onClickDone()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userType = NewEditBoardFragmentArgs.fromBundle(arguments!!).userType
        binding = FragmentBoardCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        verifyStoragePermissions(activity)
        boardPresenter = BoardPresenter(context!!)
        binding.boardCreatePrice.addTextChangedListener(CurrencyTextWatcher(binding.boardCreatePrice))
        binding.boardCreateAddPhoto.setOnClickListener(this::startGallery)
        initCategory()
        setViewPagerAdapter()
        if (userType == UserType.MAIN_USER) {
            fillFields(currentBoardViewModel.getCurrentBoard().value!!)
        }
    }

    private fun setViewPagerAdapter() {
        viewPagerAdapter = ImageViewPageAdapter(requireContext())
        viewPagerAdapter.imagePathList.addAll(
                ArrayList(currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.map {
                    val photo = PhotoModelView()
                    photo.photoInthernetPath = it.src
                    photo
                }))
        binding.boardCreateBoardPhoto.adapter = viewPagerAdapter

        dotscount = viewPagerAdapter.count

        for (i in 0 until dotscount) {
            val dot = ImageView(requireContext())
            dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 0, 8, 0)
            dots.add(dot)
            binding.boardCreateBoardPhoto.addView(dot, params)
        }

        if (!dots.isNullOrEmpty())
            dots[0].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_circle_10dp))

        binding.boardCreateBoardPhoto.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                dots.map { it.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp)) }
                dots[position].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_circle_10dp))
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun initCategory() {
        adapter = ArrayAdapter(requireContext()
                , R.layout.drop_list_item, categoriesList.map { it.nameRu })
        adapter.add("Категория")
        binding.boardCreateCategory.adapter = adapter
        binding.boardCreateCategory.setSelection(0, true)
        binding.boardCreateCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.boardCreateCategory.setSelection(position)
                categoryPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        boardPresenter!!.getCategories(object : BoardPresenter.CategoryCallback {
            override fun onSuccess(categoriesResponse: CategoryResponse) {
                categoriesList = categoriesResponse.categories
                adapter.addAll(categoriesList.map { it.nameRu })
                adapter.notifyDataSetChanged()
                if (userType == UserType.MAIN_USER) {
                    with(currentBoardViewModel.getCurrentBoard().value!!) {
                        if (category != null) {
                            binding.boardCreateCategory.setSelection(adapter.getPosition(category!!.nameRu))
                        }
                    }
                }
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fillFields(board: BoardModelView) {
        binding.boardCreateTitle.setText(board.title)
        binding.boardCreateDescription.setText(board.description)
        binding.boardCreatePrice.setText(board.cost.toString())
    }

    private fun onClickDone() {
        if (binding.boardCreateTitle.text == null || binding.boardCreateTitle.text.toString() == ""
                || binding.boardCreateDescription.text == null
                || binding.boardCreateDescription.text.toString() == "") {
            Toast.makeText(context, "Не заполнены все поля обьявления", Toast.LENGTH_SHORT).show()
        } else {
            if (userType == UserType.MAIN_NEW_USER) {
                boardPresenter!!.createBoard(object : BoardPresenter.BoardCallback {
                    override fun onSuccess(baseResponse: BaseResponse) {
                        HomeActivity.hideKeyboard(activity!!)
                        Toast.makeText(context, "Объявление создано", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, saveBoard(BoardModelView()))
            } else {
                boardPresenter!!.editBoard(object : BoardPresenter.BoardCallback {
                    override fun onSuccess(baseResponse: BaseResponse) {
                        HomeActivity.hideKeyboard(activity!!)
                        Toast.makeText(context, "Объявление изменено", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, saveBoard(currentBoardViewModel.getCurrentBoard().value!!))
            }
        }
    }

    private fun saveBoard(board: BoardModelView): BoardModelView {
        board.title = binding.boardCreateTitle.text.toString()
        board.description = binding.boardCreateDescription.text.toString()
        board.cost = binding.boardCreatePrice.text!!.toString().replace(" \u20BD".toRegex(), "").toInt()
        if (categoryPosition != 0) {
            board.category = categoriesList[categoryPosition - 1]
        }
        if (newPhotos.isNotEmpty())
            board.newPhotos.addAll(newPhotos)
        return board
    }

//    private fun onClickLoadPhoto(view: View) {
//        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2000)
//        } else {
//            startGallery(view)
//        }
//    }

    private fun startGallery(view: View) {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == 0 && resultCode == RESULT_OK && intent != null) {
            if (intent.clipData != null) {
                val photoService = PhotoService(activity!!)
                var imageStream: InputStream?

                val count = intent.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = intent.clipData!!.getItemAt(i).uri
                    imageStream = requireActivity().contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(imageStream)

                    val newPhoto = photoService.fullPreparingPhoto(bitmap, imageUri)

                    if (newPhoto != null) {
                        val dot = ImageView(requireContext())
                        dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp))
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        params.setMargins(8, 0, 8, 0)
                        dots.add(dot)
                        binding.boardCreateDotsSlider.addView(dot, params)
                        viewPagerAdapter.imagePathList.add(newPhoto)
                        viewPagerAdapter.notifyDataSetChanged()

                        newPhotos.add(newPhoto.phoneStorageFile)
                    }
                }
            }
        }
    }

    companion object {

        private val REQUEST_EXTERNAL_STORAGE = 1
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
    }
}
