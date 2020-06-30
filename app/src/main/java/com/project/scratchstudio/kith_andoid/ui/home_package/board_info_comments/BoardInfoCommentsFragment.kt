package com.project.scratchstudio.kith_andoid.ui.home_package.board_info_comments

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.UserPresenter
import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.BoardFragmentType
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.databinding.FragmentBoardInformationCommentsBinding
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.model.PhotoModelView
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.BaseResponse
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import com.project.scratchstudio.kith_andoid.ui.home_package.board_info.ImageViewPageAdapter
import com.project.scratchstudio.kith_andoid.ui.home_package.board_info_comments.list.CommentAdapter
import com.project.scratchstudio.kith_andoid.view_model.CurrentBoardViewModel
import com.project.scratchstudio.kith_andoid.view_model.CurrentUserViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class BoardInfoCommentsFragment : BaseFragment() {

    private var adapter: CommentAdapter? = null
    private lateinit var userPresenter: UserPresenter
    private lateinit var boardPresenter: BoardPresenter
    private var commentList = ArrayList<Comment>()
    private lateinit var binding: FragmentBoardInformationCommentsBinding
    private val currentBoardViewModel: CurrentBoardViewModel by activityViewModels()
    private val currentUserViewModel: CurrentUserViewModel by activityViewModels()
    private lateinit var board: BoardModelView
    private lateinit var viewPagerAdapter: ImageViewPageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBoardInformationCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userPresenter = UserPresenter(context!!)
        boardPresenter = BoardPresenter(context!!)
        board = currentBoardViewModel.getCurrentBoard().value!!
        binding.boardInfoCommentFavorite.setOnClickListener(this::onClickJoin)
        initFields()
        getComments(1, 50)

        setButtonsListener()

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.boardInfoCommentCommentsList.layoutManager = llm
        binding.boardInfoCommentCommentsList.isNestedScrollingEnabled = false

        setAdapter()
        setViewPageAdapter()

        viewPagerAdapter.imagePathList.clear()
        viewPagerAdapter.imagePathList.addAll(
                ArrayList(currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.map {
                    val photo = PhotoModelView()
                    photo.photoInthernetPath = it.src
                    photo
                }))
        viewPagerAdapter.imagePathList.addAll(
                currentBoardViewModel.getCurrentBoard().value!!.newPhotos.map {
                    it
                })
        viewPagerAdapter.notifyDataSetChanged()
//        val scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
//            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
//                getComments(page, totalItemsCount)
//            }
//        }
//        binding.boardInfoCommentCommentsList.addOnScrollListener(scrollListener)

    }

    private fun setViewPageAdapter() {
        viewPagerAdapter = ImageViewPageAdapter(requireContext(), BoardFragmentType.BOARD_INFO, null, object : ImageViewPageAdapter.OnItemClickListener {
            override fun onItemClick(item: PhotoModelView) {
                requireActivity().findNavController(R.id.nav_host_fragment_home).navigate(BoardInfoCommentsFragmentDirections
                        .actionCommentListFragmentToBoardFullScreenImageFragment(item))
            }
        })
        viewPagerAdapter.imagePathList.addAll(
                ArrayList(currentBoardViewModel.getCurrentBoard().value!!.boardPhotoUrls.map {
                    val photo = PhotoModelView()
                    photo.photoInthernetPath = it.src
                    photo
                }))
        binding.boardInfoCommentBoardPhoto.adapter = viewPagerAdapter

        binding.boardInfoCommentBoardPhoto.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
//                dots.map { it.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_active_circle_10dp)) }
//                dots[position].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_circle_10dp))
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun onClickJoin(view: View) {
        view.isEnabled = false
        if (!board.subscriptionOnBoard) {
            boardPresenter.subscribeAnnouncement(board, view as CheckBox)
        } else {
            boardPresenter.unsubscribeAnnouncement(board, view as CheckBox)
        }
    }

    private fun getComments(page: Int, size: Int) {
        boardPresenter.getComments(object : BoardPresenter.CommentsCallback {
            override fun onSuccess(commentsResponse: Array<Comment>) {
                commentsResponse.reverse()
//                                liveDataHelper!!.updateCommentList(response.comments)
                commentList.clear()
                commentList.addAll(commentsResponse)
                adapter!!.notifyDataSetChanged()
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        }, board.id, page, size)
    }

    private fun initFields() {
        binding.boardInfoCommentOwnerName.text = "${board.organizerFirstName} ${board.organizerLastName}"
        if (board.organizerPhotoUrl != "" && board.organizerPhotoUrl != "null") {
            Picasso.with(activity).load(Const.BASE_URL + board.organizerPhotoUrl)
                    .placeholder(R.mipmap.empty_photo)
                    .error(R.mipmap.empty_photo)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(binding.boardInfoCommentOwnerPhoto)
//            holder.boardPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        binding.boardInfoCommentCreateDate.text = boardPresenter.dateParser(board.startDate)
        binding.boardInfoCommentPrice.text = getString(R.string.board_info_date, board.cost.toString())
        binding.boardInfoCommentTitle.text = board.title
        if (board.subscriptionOnBoard) {
            binding.boardInfoCommentFavorite.isChecked = true
        }
    }

    private fun setAdapter() {
        adapter = CommentAdapter(requireActivity(), object : CommentAdapter.OnItemClickListener {
            override fun onItemClick(item: Comment) {
                val userModel = userPresenter.userParser(item.user, UserModelView())
//                currentUserViewModel.setCurrentUser(userPresenter.userParser(item.user, userModel))
                activity!!.findNavController(R.id.nav_host_fragment_home).navigate(BoardInfoCommentsFragmentDirections
                        .actionCommentListFragmentToProfileFragment(userModel))
            }
        })
        adapter!!.setCommentList(commentList)
        binding.boardInfoCommentCommentsList.adapter = adapter
    }

    private fun setButtonsListener() {
        binding.boardInfoCommentButtonSend.setOnClickListener {
            boardPresenter.sendComment(object : BoardPresenter.BoardCallback {
                override fun onSuccess(baseResponse: BaseResponse) {
                    getComments(1, 50)
                    binding.boardInfoCommentAddCommentField.text.clear()
                    HomeActivity.hideKeyboard(activity!!)
                    board.chatCount += 1
                    currentBoardViewModel.setCurrentBoard(board)
                }

                override fun onError(networkError: NetworkErrorException) {
                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                }
            }, board.id, binding.boardInfoCommentAddCommentField.text.toString())
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): BoardInfoCommentsFragment {
            val fragment = BoardInfoCommentsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
