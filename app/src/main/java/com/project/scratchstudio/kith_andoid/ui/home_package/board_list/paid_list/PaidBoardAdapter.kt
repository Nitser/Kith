package com.project.scratchstudio.kith_andoid.ui.home_package.board_list.paid_list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.model.BoardModelView
import com.project.scratchstudio.kith_andoid.ui.home_package.board_list.paid_list.PaidBoardHolder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.util.ArrayList

class PaidBoardAdapter(private val activity: Activity, private val listener: OnItemClickListener, private val boardPresenter: BoardPresenter) : RecyclerView.Adapter<PaidBoardHolder>() {
    var annList: ArrayList<BoardModelView> = ArrayList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PaidBoardHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_list_paid_item_layout, viewGroup, false)
        return PaidBoardHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaidBoardHolder, i: Int) {
        val board = annList[i]
        val ownerName = "${board.organizerFirstName} ${board.organizerLastName}"

        holder.title.text = board.title
        holder.ownerName.text = ownerName
        holder.favoriteIcon.isChecked = board.subscriptionOnBoard
        holder.price.text = activity.getString(R.string.board_list_date, board.cost.toString())
        holder.chatNumber.text = board.chatCount.toString()
        holder.date.text = boardPresenter.dateParser(board.startDate)

        if (board.organizerPhotoUrl != "" && board.organizerPhotoUrl != "null") {
            Picasso.with(activity).load(Const.BASE_URL + board.organizerPhotoUrl)
                    .placeholder(R.mipmap.empty_photo)
                    .error(R.mipmap.empty_photo)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.ownerPhoto)
            holder.boardPhoto.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        if (board.boardPhotoUrls.isNotEmpty()) {
            Picasso.with(activity).load(Const.BASE_URL + board.boardPhotoUrls[0].src)
                    .placeholder(R.color.colorEmptyPhoto)
                    .error(R.color.colorEmptyPhoto)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.boardPhoto)
            holder.boardPhoto.scaleType = ImageView.ScaleType.CENTER_INSIDE
        } else {
            holder.boardPhoto.setImageResource(R.color.colorEmptyPhoto)
        }

        if (board.subscriptionOnBoard)
            holder.favoriteIcon.isChecked = true

        holder.favoriteIcon.setOnClickListener {
            if (!board.subscriptionOnBoard) {
                boardPresenter.subscribeAnnouncement(board, holder.favoriteIcon)
            } else {
                boardPresenter.unsubscribeAnnouncement(board, holder.favoriteIcon)
            }
        }

        holder.bind(board, listener, i)
    }

    override fun getItemCount(): Int {
        return annList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnItemClickListener {
        fun onItemClick(item: BoardModelView, id: Int, boardHolder: PaidBoardHolder)
    }

}
