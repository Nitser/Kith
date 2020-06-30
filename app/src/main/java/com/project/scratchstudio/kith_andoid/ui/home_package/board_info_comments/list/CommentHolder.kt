package com.project.scratchstudio.kith_andoid.ui.home_package.board_info_comments.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import de.hdodenhof.circleimageview.CircleImageView

class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var ownerName: TextView = itemView.findViewById(R.id.comment_item_owner_name)
    var date: TextView = itemView.findViewById(R.id.comment_item_create_date)
    var comment: TextView = itemView.findViewById(R.id.comment_item_text)
    var photo: CircleImageView = itemView.findViewById(R.id.comment_item_owner_photo)

    fun bind(item: Comment, listener: CommentAdapter.OnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item) }
    }
}
