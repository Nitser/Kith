package com.project.scratchstudio.kith_andoid.ui.home_package.board_info_comments.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.util.ArrayList

class CommentAdapter(private val activity: Activity) : RecyclerView.Adapter<CommentHolder>() {

    private var commentList: ArrayList<Comment> = ArrayList()
    private var boardPresenter = BoardPresenter(activity)

    interface OnItemClickListener {
        fun onItemClick(item: Comment)
    }

    fun setCommentList(list: ArrayList<Comment>) {
        commentList = list
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CommentHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.comment_item, viewGroup, false)
        return CommentHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentHolder, i: Int) {
        val comment = commentList[i]
        holder.ownerName.text = "${comment.user.firstName} ${comment.user.lastName}"
        holder.date.text = boardPresenter.dateParser(comment.timestamp)
        holder.comment.text = comment.message
        if (comment.user.photo != "" && comment.user.photo != null)
            Picasso.with(activity).load(comment.user.photo!!.replace("@[0-9]*".toRegex(), ""))
                    .transform(PicassoCircleTransformation())
                    .placeholder(R.mipmap.empty_photo)
                    .error(R.mipmap.empty_photo)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.photo)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

}
