package com.project.scratchstudio.kith_andoid.ui.home_package.home.invited_user_list

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.custom_views.to_trash.CustomFontTextView
import com.project.scratchstudio.kith_andoid.model.UserModelView

class InvitedUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var image: ImageView = itemView.findViewById(R.id.photo)
    var name: CustomFontTextView = itemView.findViewById(R.id.name)
    var position: CustomFontTextView = itemView.findViewById(R.id.position)

    fun bind(item: UserModelView, listener: InvitedUserAdapter.OnItemClickListener) {
        itemView.setOnClickListener { listener.onItemClick(item) }
    }
}
