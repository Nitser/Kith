package com.project.scratchstudio.kith_andoid.ui.home_package.Comments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView
import com.project.scratchstudio.kith_andoid.CustomViews.EndlessRecyclerViewScrollListener
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.FragmentType
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.LiveDataHelper
import com.project.scratchstudio.kith_andoid.network.apiService.CommentApi
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import com.project.scratchstudio.kith_andoid.network.model.comment.CommentResponse
import com.project.scratchstudio.kith_andoid.ui.home_package.Comments.list.CommentAdapter
import com.project.scratchstudio.kith_andoid.ui.home_package.NewComment.NewCommentFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CommentListFragment : BaseFragment() {
    private var bundle: Bundle? = null
    private var boardId: Int = 0
    private var boardTitle: String? = null
    private var listView: RecyclerView? = null
    private var adapter: CommentAdapter? = null

    private var commentApi: CommentApi? = null
    private val disposable = CompositeDisposable()
    private var liveDataHelper: LiveDataHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bundle = arguments
        boardId = bundle!!.getInt("board_id")
        boardTitle = bundle!!.getString("board_title")
        return inflater.inflate(R.layout.fragment_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        liveDataHelper = LiveDataHelper.instance
//        val sum: (Int, Int) -> Int = { x: Int, y: Int -> x + y }
        val commentObserver = Observer<MutableList<Comment>> { commentList ->
            if (adapter!!.itemCount == 0) {
                adapter!!.setCommentList(commentList)
            }
            adapter!!.setCommentList(commentList)
            adapter!!.notifyDataSetChanged()
        }
        LiveDataHelper.instance.observeCommentList().observe(this, commentObserver)

        commentApi = ApiClient.getClient(context!!).create<CommentApi>(CommentApi::class.java)
        getComments(0, 10)

        setButtonsListener()

        listView = activity!!.findViewById(R.id.listDialog)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        listView!!.layoutManager = llm
        val scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                getComments(page, totalItemsCount)
            }
        }
        listView!!.addOnScrollListener(scrollListener)
        setAdapter()

        val title = activity!!.findViewById<CustomFontTextView>(R.id.title)
        title.text = boardTitle
    }

    private fun getComments(page: Int, size: Int) {
        disposable.add(
                commentApi!!.getComments(boardId, page.toString(), size.toString(), "2030-06-22 22:22:22")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<CommentResponse>() {
                            override fun onSuccess(response: CommentResponse) {
                                for (comment in response.comments) {
                                    comment.user.photo = comment.user.photo.replace("\\/".toRegex(), "/")
                                }
                                response.comments.reverse()
                                liveDataHelper!!.updateCommentList(response.comments)
                            }

                            override fun onError(e: Throwable) {
                                Log.e("GetComment Resp", "onError: " + e.message)
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        })
        )
    }

    private fun setAdapter() {
        adapter = CommentAdapter(activity!!)
        listView!!.adapter = adapter
    }

    private fun setButtonsListener() {
        val back = activity!!.findViewById<ImageButton>(R.id.back_comment)
        back.setOnClickListener { this.onClickBack(it) }
        val send = activity!!.findViewById<LinearLayout>(R.id.new_c)
        send.setOnClickListener { this.onClickSend(it) }
    }

    private fun onClickBack(view: View) {
        (activity as HomeActivity).backFragment()
    }

    fun onClickSend(view: View) {
        (activity as HomeActivity).addFragment(NewCommentFragment.newInstance(bundle!!), FragmentType.NEW_COMMENT.name)
    }

    override fun onDestroyView() {
        liveDataHelper!!.clearCommentList()
        super.onDestroyView()
    }

    fun reloadComments() {
        liveDataHelper!!.clearCommentList()
        getComments(0, 10)
    }

    companion object {

        fun newInstance(bundle: Bundle): CommentListFragment {
            val fragment = CommentListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
