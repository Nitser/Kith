package com.project.scratchstudio.kith_andoid.ui.BoardList.list

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.board.Board

class BoardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var image: ImageView = itemView.findViewById(R.id.photo)
    var title: CustomFontTextView = itemView.findViewById(R.id.title)
    var date: CustomFontTextView = itemView.findViewById(R.id.date)
    var favorite: CheckBox = itemView.findViewById(R.id.favorite)

    fun bind(item: Board, listener: BoardAdapter.OnItemClickListener, id: Int) {
        itemView.setOnClickListener { v -> listener.onItemClick(item, id, this) }
    }
}
