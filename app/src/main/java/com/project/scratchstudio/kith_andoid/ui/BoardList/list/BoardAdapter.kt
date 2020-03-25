package com.project.scratchstudio.kith_andoid.ui.BoardList.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.network.model.board.Board
import com.project.scratchstudio.kith_andoid.ui.BoardList.BoardsFragment
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.util.ArrayList

class BoardAdapter(private val activity: Activity, private val listener: OnItemClickListener, private val fragment: BoardsFragment) : RecyclerView.Adapter<BoardHolder>() {
    var annList: List<Board> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): BoardHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_list_item_layout, viewGroup, false)
        return BoardHolder(itemView)
    }

    override fun onBindViewHolder(holder: BoardHolder, i: Int) {
        val board = annList[i]
        holder.title.text = board.title
        holder.favorite.isChecked = board.subscriptionOnBoard == 1

        if (board.url != "" && board.url != "null") {
            Picasso.with(activity).load(board.url)
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image)
            holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        holder.favorite.setOnClickListener {
            holder.favorite.isEnabled = false
            if (board.subscriptionOnBoard != 1) {
                fragment.subscribeAnnouncement(HomeActivity.mainUser!!.id, board, holder.favorite)
            } else {
                fragment.unsubscribeAnnouncement(HomeActivity.mainUser!!.id, board, holder.favorite)
            }
        }

        holder.bind(board, listener, i)
    }

    override fun getItemCount(): Int {
        return annList.size
    }

    interface OnItemClickListener {
        fun onItemClick(item: Board, id: Int, boardHolder: BoardHolder)
    }

}
