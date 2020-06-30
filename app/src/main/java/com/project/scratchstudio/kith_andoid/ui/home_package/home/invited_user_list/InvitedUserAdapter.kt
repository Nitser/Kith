package com.project.scratchstudio.kith_andoid.ui.home_package.home.invited_user_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.utils.PicassoCircleTransformation
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

class InvitedUserAdapter(private val context: Context, private val listener: OnItemClickListener) : RecyclerView.Adapter<InvitedUserHolder>() {

    var invitedUsers: ArrayList<UserModelView> = ArrayList()

    init {
        setHasStableIds(true)
    }

    interface OnItemClickListener {
        fun onItemClick(item: UserModelView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): InvitedUserHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_layout, viewGroup, false)
        return InvitedUserHolder(itemView)
    }

    override fun onBindViewHolder(invitedUserHolder: InvitedUserHolder, i: Int) {
        val invitedUser = invitedUsers[i]
        val name = "${invitedUser.firstName} ${invitedUser.lastName}"
        invitedUserHolder.name.text = name
        invitedUserHolder.position.text = invitedUser.position
        if (invitedUser.photo != "") {
            Picasso.with(context).load(Const.BASE_URL + invitedUser.photo)
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(invitedUserHolder.image)
        }
        invitedUserHolder.bind(invitedUser, listener)
    }

    override fun getItemCount(): Int {
        return invitedUsers.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}
