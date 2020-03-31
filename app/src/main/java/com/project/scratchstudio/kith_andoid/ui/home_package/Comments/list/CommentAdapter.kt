package com.project.scratchstudio.kith_andoid.ui.home_package.Comments.list

import android.app.Activity
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.util.ArrayList
import java.util.Date

class CommentAdapter(private val activity: Activity) : RecyclerView.Adapter<CommentHolder>() {

    private var commentList: List<Comment> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(item: Comment)
    }

    fun setCommentList(list: List<Comment>) {
        commentList = list
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CommentHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.message_dialog_item_layout, viewGroup, false)
        return CommentHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentHolder, i: Int) {
        val comment = commentList[i]
        holder.name.text = comment.user!!.lastName + " " + comment.user!!.firstName
        holder.position.text = comment.user!!.position
        holder.time.text = comment.timestamp!!.replace("[-0-9]*\\s".toRegex(), "").replace(":[0-9]*$".toRegex(), "")

        try {
            val inputFormat: DateFormat
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = SimpleDateFormat("yyyy-MM-dd")
                val outputFormat = SimpleDateFormat("dd.MM.yyyy")
                val newDateFormat: Date = inputFormat.parse(comment.timestamp!!.replace("\\s.*$".toRegex(), ""))
                val outputDateStr = outputFormat.format(newDateFormat)
                holder.date.text = outputDateStr
            } else {
                holder.date.setText(comment.timestamp!!.replace("\\s.*$".toRegex(), ""))
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }


        holder.comment.text = comment.message
        Picasso.with(activity).load(comment.user!!.photo!!.replace("@[0-9]*".toRegex(), ""))
                .transform(PicassoCircleTransformation())
                .error(R.mipmap.person)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(holder.photo)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

}
