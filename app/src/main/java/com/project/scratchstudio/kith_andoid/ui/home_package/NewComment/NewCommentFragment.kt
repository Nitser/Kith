package com.project.scratchstudio.kith_andoid.ui.home_package.NewComment

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

import com.project.scratchstudio.kith_andoid.activities.HomeActivity
import com.project.scratchstudio.kith_andoid.custom_views.CustomFontEditText
import com.project.scratchstudio.kith_andoid.custom_views.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.network.ApiClient
import com.project.scratchstudio.kith_andoid.network.apiService.CommentApi
import com.project.scratchstudio.kith_andoid.network.model.comment.SendCommentResponse
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

import java.util.Objects
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class NewCommentFragment : BaseFragment() {
    private var bundle: Bundle? = null
    private var boardId: Int = 0

    private var commentApi: CommentApi? = null
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bundle = arguments
        boardId = bundle!!.getInt("board_id")
        return inflater.inflate(R.layout.fragment_new_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        commentApi = ApiClient.getClient(context!!).create<CommentApi>(CommentApi::class.java!!)
        setButtonsListener()
        init()
    }

    private fun init() {
        val name = activity!!.findViewById<CustomFontTextView>(R.id.new_comment_name)
        val position = activity!!.findViewById<CustomFontTextView>(R.id.new_comment_position)
        val photo = activity!!.findViewById<ImageView>(R.id.new_comment_photo)
        val mainUser = HomeActivity.mainUser

        name.text = mainUser.firstName
        position.text = mainUser.position
        Picasso.with(context).load(mainUser.photo.replace("@[0-9]*".toRegex(), ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(photo)
    }

    private fun setButtonsListener() {
        val cancel = activity!!.findViewById<CustomFontTextView>(R.id.new_comment_cancel)
        cancel.setOnClickListener { this.onClickBack(it) }
        val send = activity!!.findViewById<CustomFontTextView>(R.id.new_comment_done)
        send.setOnClickListener { this.onClickSend(it) }
    }

    fun onClickBack(view: View?) {
        (activity as HomeActivity).backFragment()
    }

    private fun onClickSend(view: View) {
        val userMessage = activity!!.findViewById<CustomFontEditText>(R.id.new_comment_message)
        val message = Objects.requireNonNull<Editable>(userMessage.text).toString()
        if (message.trim { it <= ' ' }.isNotEmpty()) {
            val send = activity!!.findViewById<CustomFontTextView>(R.id.new_comment_done)
            send.isEnabled = false
            disposable.add(
                    commentApi!!.sendComment(boardId, message)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableSingleObserver<SendCommentResponse>() {
                                override fun onSuccess(response: SendCommentResponse) {
                                    if (response.status) {
                                        (activity as HomeActivity).updateComments()
                                        userMessage.clearFocus()
                                        onClickBack(null)
                                    } else {
                                        Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                        send.isEnabled = true
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Log.e("SendComment Resp", "onError: " + e.message)
                                    Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                                    send.isEnabled = true
                                }
                            })
            )
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): NewCommentFragment {
            val fragment = NewCommentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}
