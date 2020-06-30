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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.BoardFragmentType
import com.project.scratchstudio.kith_andoid.app.UserType
import com.project.scratchstudio.kith_andoid.databinding.FragmentBoardCreateBinding
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.category.Category
import com.project.scratchstudio.kith_andoid.network.model.category.CategoryResponse
import com.project.scratchstudio.kith_andoid.utils.PhotoService
import com.project.scratchstudio.kith_andoid.ui.home_package.board_info.ImageViewPageAdapter
import com.project.scratchstudio.kith_andoid.view_model.CurrentBoardViewModel
import java.io.InputStream

class NewEditBoardFragment : BaseFragment() {
    private lateinit var binding: FragmentBoardCreateBinding
    private lateinit var userType: UserType
    private val currentBoardViewModel: CurrentBoardViewModel by activityViewModels()

    private var boardPresenter: BoardPresenter? = null

    private lateinit var adapter: ArrayAdapter<CharSequence>
    private var categoriesList = ArrayList<Category>()
    private var categoryPosition: Int = 0

    private lateinit var viewPagerAdapter: ImageViewPageAdapter
    private val deletedUri = ArrayList<String>()
//    private var dots = ArrayList<ImageView>()
//    private var startDotIndex = 0

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
        verifyStoragePermissions(requireActivity())
        boardPresenter = BoardPresenter(requireContext())

        binding.boardCreatePrice.addTextChangedListener(CurrencyTextWatcher(binding.boardCreatePrice))
        binding.boardCreateAddPhoto.setOnClickListener(this::startGallery)

        initCategory()
        setViewPagerAdapter()

        if (userType == UserType.MAIN_USER) {
            fillFields(currentBoardViewModel.getCurrentBoard().value!!)
        }

        if (currentBoardViewModel.getNewPhoto().value != null && !currentBoardViewModel.getNewPhoto().value!!.isNullOrEmpty()) {
            currentBoardViewModel.getNewPhoto().value!!.clear()
        } else {
            currentBoardViewModel.setNewPhoto(ArrayList())
        }
        currentBoardViewModel.getNewPhoto().observe(viewLifecycleOwner, Observer<ArrayList<PhotoModelView>> { item ->
            viewPagerAdapter.imagePathList.clear()
            viewPagerAdapter.imagePathList.addAll(currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.map {
                val photo = PhotoModelView()
                photo.id = it.id
                photo.photoInthernetPath = it.src
                photo
            })
            viewPagerAdapter.imagePathList.addAll(
                    ArrayList(item.map {
                        val photo = PhotoModelView()
                        photo.photoBitmap = it.photoBitmap
                        photo
                    }))
//            initDoteView(viewPagerAdapter.imagePathList.size, startDotIndex)
        })
    }

    private fun setViewPagerAdapter() {
        viewPagerAdapter = ImageViewPageAdapter(requireContext(), BoardFragmentType.BOARD_EDIT
                , object : ImageViewPageAdapter.OnItemClickListener {
            override fun onItemClick(item: PhotoModelView) {
                if (item.id != -1) {
                    boardPresenter!!.deletePhoto(object : BoardPresenter.BoardCallback {
                        override fun onSuccess(baseResponse: BaseResponse) {
                            viewPagerAdapter.imagePathList.remove(item)
                            viewPagerAdapter.notifyDataSetChanged()
                            currentBoardViewModel.getNewPhoto().value!!.remove(item)
                            deletedUri.add(item.photoInthernetPath)
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(requireContext(), "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }, item.id)
                } else {
                    viewPagerAdapter.imagePathList.remove(item)
                    viewPagerAdapter.notifyDataSetChanged()
                    currentBoardViewModel.getNewPhoto().value!!.remove(item)
                    deletedUri.add(item.photoInthernetPath)
                }
            }
        }, null)
        binding.boardCreateBoardPhoto.adapter = viewPagerAdapter

        binding.boardCreateBoardPhoto.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
//                startDotIndex = position
//                dots.map { it.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp)) }
//                dots[position].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_circle_10dp))
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun initDoteView(size: Int, startIndex: Int) {
//        binding.boardCreateDotsSlider.removeAllViews()
//
//        if (size > 0)
//            for (i in 0 until size) {
//                val dot = ImageView(requireContext())
//                if (i == startIndex) {
//                    dots[startIndex].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_circle_10dp))
//                } else {
//                    dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp))
//                }
//                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                params.setMargins(8, 0, 8, 0)
//                dots.add(dot)
//                binding.boardCreateDotsSlider.addView(dot, params)
//            }
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
        viewPagerAdapter.imagePathList.addAll(
                ArrayList(currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.map {
                    val photo = PhotoModelView()
                    photo.photoInthernetPath = it.src
                    photo
                }))
        viewPagerAdapter.notifyDataSetChanged()
//        initDoteView(viewPagerAdapter.imagePathList.size, startDotIndex)
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
                        HomeActivity.hideKeyboard(requireActivity())
                        Toast.makeText(context, "Объявление создано", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }

                    override fun onError(networkError: NetworkErrorException) {
                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                    }
                }, saveBoard(currentBoardViewModel.getCurrentBoard().value!!))
            } else {
                boardPresenter!!.editBoard(object : BoardPresenter.BoardCallback {
                    override fun onSuccess(baseResponse: BaseResponse) {
                        HomeActivity.hideKeyboard(requireActivity())
                        Toast.makeText(context, "Объявление изменено", Toast.LENGTH_SHORT).show()
//                        currentBoardViewModel.getCurrentBoard().value!!.newPhotos.addAll(currentBoardViewModel.getNewPhoto().value!!)
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
        if (currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.isNotEmpty()) {
            val res = currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.filter {
                deletedUri.contains(it.src)
            }
            currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.removeAll(res)
        }
        if (currentBoardViewModel.getNewPhoto().value!!.isNotEmpty())
            board.newPhotos.addAll(currentBoardViewModel.getNewPhoto().value!!)
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
        if (currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.size + currentBoardViewModel.getNewPhoto().value!!.size < 10) {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        } else {
            Toast.makeText(requireContext(), "Лимит количества фотографий достигнут", Toast.LENGTH_SHORT).show()
        }
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
                        newPhoto.photoBitmap = photoService.changePhoto(newPhoto.photoBitmap!!, newPhoto.phoneStorageFile)
//                        val dot = ImageView(requireContext())
//                        dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp))
//                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                        params.setMargins(8, 0, 8, 0)
//                        dots.add(dot)
//                        binding.boardCreateDotsSlider.addView(dot, params)
                        viewPagerAdapter.imagePathList.add(newPhoto)
                        viewPagerAdapter.notifyDataSetChanged()

                        currentBoardViewModel.getNewPhoto().value!!.add(newPhoto)
                    }
                }
            }
        }
    }

    companion object {

        private const val REQUEST_EXTERNAL_STORAGE = 1
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
    }
}
