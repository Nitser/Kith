package com.project.scratchstudio.kith_andoid.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup

import com.project.scratchstudio.kith_andoid.holders.TreeHolder
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.service.PicassoCircleTransformation
import com.project.scratchstudio.kith_andoid.network.model.user.User
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import androidx.recyclerview.widget.RecyclerView

class TreeAdapter(private val activity: Activity, private var userList: List<User>?, private val listener: OnItemClickListener) : RecyclerView.Adapter<TreeHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: User)
    }

    fun setUserList(list: List<User>) {
        userList = list
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TreeHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_layout, viewGroup, false)
        return TreeHolder(itemView)
    }

    override fun onBindViewHolder(holder: TreeHolder, i: Int) {
        val user = userList!![i]
        val name = user.firstName + " " + user.lastName
        holder.name.text = name
        holder.position.text = user.position

        Picasso.with(activity).load(user.photo!!.replace("@[0-9]*".toRegex(), ""))
                .placeholder(R.mipmap.person)
                .error(R.mipmap.person)
                .transform(PicassoCircleTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(holder.image)

        holder.bind(user, listener)
    }

    override fun getItemCount(): Int {
        return userList!!.size
    }

}
