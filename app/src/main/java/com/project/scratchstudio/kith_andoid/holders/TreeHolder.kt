package com.project.scratchstudio.kith_andoid.holders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.adapters.TreeAdapter
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.user.User

class TreeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var image: ImageView = itemView.findViewById(R.id.photo)
    var name: CustomFontTextView = itemView.findViewById(R.id.name)
    var position: CustomFontTextView = itemView.findViewById(R.id.position)
    private val cardView: CardView = itemView.findViewById(R.id.card_view)

    init {

        cardView.setCardBackgroundColor(Color.TRANSPARENT)
        cardView.cardElevation = 0f

    }

    fun bind(item: User, listener: TreeAdapter.OnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item) }
    }
}
