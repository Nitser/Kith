package com.project.scratchstudio.kith_andoid.ui.home_package.board_info_comments.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import de.hdodenhof.circleimageview.CircleImageView

class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var ownerName: TextView
    var date: TextView
    var comment: TextView
    var photo: CircleImageView

    init {
        ownerName = itemView.findViewById(R.id.comment_item_owner_name)
        date = itemView.findViewById(R.id.comment_item_create_date)
        comment = itemView.findViewById(R.id.comment_item_text)
        photo = itemView.findViewById(R.id.comment_item_owner_photo)
    }

    fun bind(item: Comment, listener: CommentAdapter.OnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item) }
    }
}
