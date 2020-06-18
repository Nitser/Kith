package com.project.scratchstudio.kith_andoid.ui.home_package.home

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontTextView
import com.project.scratchstudio.kith_andoid.model.UserModelView

class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var nName: CustomFontTextView = itemView.findViewById(R.id.name)
    var nPosition: CustomFontTextView = itemView.findViewById(R.id.position)
    var nPhoto: ImageView = itemView.findViewById(R.id.photo)
    private val cardView: CardView = itemView.findViewById(R.id.card_view)

    init {

        cardView.setCardBackgroundColor(Color.TRANSPARENT)
        cardView.cardElevation = 0f
    }

    fun bind(item: UserModelView, listener: SearchAdapter.OnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item) }
    }
}
