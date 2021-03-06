package com.project.scratchstudio.kith_andoid.holders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.adapters.MessageDialogAdapter
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontTextView
import com.project.scratchstudio.kith_andoid.model.AnnouncementInfo

class MessageDialogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var nTitle: CustomFontTextView = itemView.findViewById(R.id.title)
    var nMessage: CustomFontTextView = itemView.findViewById(R.id.message)
    var nPhoto: ImageView = itemView.findViewById(R.id.photo)
    private val cardView: CardView = itemView.findViewById(R.id.card_view)

    init {

        cardView.setCardBackgroundColor(Color.TRANSPARENT)
        cardView.cardElevation = 0f
    }

    fun bind(item: AnnouncementInfo, listener: MessageDialogAdapter.OnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item) }
    }
}
