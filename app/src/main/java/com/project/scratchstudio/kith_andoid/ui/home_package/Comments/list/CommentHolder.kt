package com.project.scratchstudio.kith_andoid.ui.home_package.Comments.list

import android.graphics.Color
import android.view.View
import android.widget.ImageView

import com.project.scratchstudio.kith_andoid.custom_views.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var name: CustomFontTextView
    var position: CustomFontTextView
    var time: CustomFontTextView
    var date: CustomFontTextView
    var comment: CustomFontTextView
    var photo: ImageView
    private val cardView: CardView

    init {
        name = itemView.findViewById(R.id.name)
        position = itemView.findViewById(R.id.position)
        time = itemView.findViewById(R.id.time)
        date = itemView.findViewById(R.id.date)
        comment = itemView.findViewById(R.id.comment)

        photo = itemView.findViewById(R.id.photo)

        cardView = itemView.findViewById(R.id.card_view)
        cardView.setCardBackgroundColor(Color.parseColor("#eff1f2"))
        cardView.cardElevation = 0f
    }

    fun bind(item: Comment, listener: CommentAdapter.OnItemClickListener) {
        itemView.setOnClickListener { v -> listener.onItemClick(item) }
    }
}
