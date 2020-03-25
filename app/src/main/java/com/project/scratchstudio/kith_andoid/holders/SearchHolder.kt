package com.project.scratchstudio.kith_andoid.holders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.user.User

class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var nName: CustomFontTextView = itemView.findViewById(R.id.name)
    var nPosition: CustomFontTextView = itemView.findViewById(R.id.position)
    var nPhoto: ImageView = itemView.findViewById(R.id.photo)
    private val cardView: CardView = itemView.findViewById(R.id.card_view)

    init {

        cardView.setCardBackgroundColor(Color.TRANSPARENT)
        cardView.cardElevation = 0f
    }

    fun bind(item: User, listener: SearchAdapter.OnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item) }
    }
}
