package com.project.scratchstudio.kith_andoid.ui.home_package.board_list.paid_list

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import de.hdodenhof.circleimageview.CircleImageView

class PaidBoardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var ownerPhoto: CircleImageView = itemView.findViewById(R.id.board_card_owner_photo)
    var ownerName: TextView = itemView.findViewById(R.id.board_card_owner_name)
    var title: TextView = itemView.findViewById(R.id.board_card_title)
    var boardPhoto: ImageView = itemView.findViewById(R.id.board_card_board_photo)
    var price: TextView = itemView.findViewById(R.id.board_card_price)
    var date: TextView = itemView.findViewById(R.id.board_card_create_date)
    var favoriteIcon: CheckBox = itemView.findViewById(R.id.board_card_favorite)
    var chatNumber: TextView = itemView.findViewById(R.id.board_card_chat_number)

    fun bind(item: BoardModelView, listener: PaidBoardAdapter.OnItemClickListener, id: Int) {
        itemView.setOnClickListener { listener.onItemClick(item, id, this) }
    }
}
