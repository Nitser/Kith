package com.project.scratchstudio.kith_andoid.ui.home_package.board_info

import android.accounts.NetworkErrorException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.app.UserType
import com.project.scratchstudio.kith_andoid.databinding.FragmentBoardInformationBinding
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.project.scratchstudio.kith_andoid.view_model.CurrentBoardViewModel
import com.project.scratchstudio.kith_andoid.view_model.CurrentUserViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class BoardInfoFragment : BaseFragment() {

    private val currentUserViewModel: CurrentUserViewModel by activityViewModels()
    private val currentBoardViewModel: CurrentBoardViewModel by activityViewModels()
    private lateinit var testBoard: BoardModelView
    private lateinit var binding: FragmentBoardInformationBinding
    private lateinit var userPresenter: UserPresenter
    private lateinit var boardPresenter: BoardPresenter
    private lateinit var userType: UserType

    private var dotscount: Int = 0
    private var dots = ArrayList<ImageView>()

    private var lastDotIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board_info, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_board_info_edit_board -> {
                activity!!.findNavController(R.id.nav_host_fragment_home)
                        .navigate(BoardInfoFragmentDirections.actionBoardInfoFragmentToNewEditBoardFragment(userType))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBoardInformationBinding.inflate(inflater, container, false)
        userType = BoardInfoFragmentArgs.fromBundle(arguments!!).userType
        return binding.root
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewPageAdapter()
        userPresenter = UserPresenter(context!!)
        boardPresenter = BoardPresenter(context!!)
        setButtonsListener()
        testBoard = currentBoardViewModel.getCurrentBoard().value!!
        currentBoardViewModel.getCurrentBoard().observe(viewLifecycleOwner, Observer<BoardModelView> { item ->
            initFields(item)
            testBoard = item
        })
        initFields(currentBoardViewModel.getCurrentBoard().value!!)
    }

    private fun setViewPageAdapter() {
        val viewPagerAdapter = ImageViewPageAdapter(requireContext())
        viewPagerAdapter.imagePathList.addAll(
                ArrayList(currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.map {
                    val photo = PhotoModelView()
                    photo.photoInthernetPath = it.src
                    photo
                }))
        binding.boardInfoBoardPhoto.adapter = viewPagerAdapter

        dotscount = viewPagerAdapter.count

        for (i in 0 until dotscount) {
            val dot = ImageView(requireContext())
            dot.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(8, 0, 8, 0)
            dots.add(dot)
            binding.boardInfoDotsSlider.addView(dot, params)
        }

        if (!dots.isNullOrEmpty())
            dots[0].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_circle_10dp))

        binding.boardInfoBoardPhoto.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                dots.map { it.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp)) }
                dots[position].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_circle_10dp))
                Log.i("DOTS", "position = $position")
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun setButtonsListener() {
        binding.boardInfoFavorite.setOnClickListener(this::onClickJoin)
        binding.boardInfoChatButton.setOnClickListener(this::onClickComments)
        binding.boardInfoOwnerPhoto.setOnClickListener(this::onClickProfile)
        binding.boardInfoButtonPhoneLayout.setOnClickListener(this::onClickCallPhone)
    }

    private fun initFields(board: BoardModelView) {
        binding.boardInfoOwnerName.text = "${board.organizerFirstName} ${board.organizerLastName}"
        binding.boardInfoCreateDate.text = boardPresenter.dateParser(board.startDate)
        binding.boardInfoPrice.text = getString(R.string.board_info_date, board.cost.toString())
        binding.boardInfoDescription.text = board.description
        binding.boardInfoChatNumber.text = board.chatCount.toString()
        binding.boardInfoTitle.text = board.title
        if (board.subscriptionOnBoard) {
            binding.boardInfoFavorite.isChecked = true
        }

        if (board.organizerPhotoUrl != "" && board.organizerPhotoUrl != "null") {
            Picasso.with(activity).load(Const.BASE_URL + board.organizerPhotoUrl)
                    .placeholder(R.mipmap.empty_photo)
                    .error(R.mipmap.empty_photo)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding.boardInfoOwnerPhoto)
//            holder.boardPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        }

//        if (info!!.url != null && info!!.url != "null" && info!!.url != "") {
//            Picasso.with(activity).load(info!!.url!!.replace("@[0-9]*".toRegex(), ""))
//                    .error(R.drawable.newspaper)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                    .into(photo)
//            photo!!.scaleType = ImageView.ScaleType.CENTER_CROP
//        }
    }

    private fun onClickProfile(view: View) {
        userPresenter.getUser(object : UserPresenter.GetUserCallback {
            override fun onSuccess(userResponse: User) {
                val userModel = UserModelView()
                currentUserViewModel.setCurrentUser(userPresenter.userParser(userResponse, userModel))
                activity!!.findNavController(R.id.nav_host_fragment_home).navigate(BoardInfoFragmentDirections
                        .actionBoardInfoFragmentToProfileFragment(userType))
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, testBoard.organizerId)
    }

    private fun onClickCallPhone(view: View) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + currentBoardViewModel.getCurrentBoard().value!!.organizerPhone)
        startActivity(intent)
    }

    private fun onClickComments(view: View) {
        activity!!.findNavController(R.id.nav_host_fragment_home).navigate(BoardInfoFragmentDirections
                .actionBoardInfoFragmentToCommentListFragment())
    }

    private fun onClickJoin(view: View) {
        view.isEnabled = false
        if (!testBoard.subscriptionOnBoard) {
            boardPresenter.subscribeAnnouncement(testBoard, view as CheckBox)
        } else {
            boardPresenter.unsubscribeAnnouncement(testBoard, view as CheckBox)
        }
    }

    companion object {

        fun newInstance(): BoardInfoFragment {
            return BoardInfoFragment()
        }
    }
}
