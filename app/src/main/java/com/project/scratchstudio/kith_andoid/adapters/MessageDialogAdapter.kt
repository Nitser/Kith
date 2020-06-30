package com.project.scratchstudio.kith_andoid.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.holders.MessageDialogHolder
import com.project.scratchstudio.kith_andoid.model.AnnouncementInfo
import com.project.scratchstudio.kith_andoid.utils.PicassoCircleTransformation
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.util.ArrayList

class MessageDialogAdapter(private val activity: Activity, private val annList: List<AnnouncementInfo>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MessageDialogHolder>(), Filterable {
    private var filteredList: List<AnnouncementInfo>? = null

    interface OnItemClickListener {
        fun onItemClick(item: AnnouncementInfo)
    }

    init {
        filteredList = annList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MessageDialogHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.message_list_item_layout, viewGroup, false)
        return MessageDialogHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageDialogHolder, i: Int) {
        val msgInfo = annList[i]
        holder.nTitle.text = msgInfo.title
        holder.nMessage.text = "some msg"

        if (msgInfo.url != "" && msgInfo.url != "null" && msgInfo.url != "") {
            Picasso.with(activity).load(msgInfo.url.replace("@[0-9]*".toRegex(), ""))
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.newspaper)
                    .error(R.drawable.newspaper)
                    .into(holder.nPhoto)
        }

        holder.bind(msgInfo, listener)
    }

    override fun getItemCount(): Int {
        return filteredList!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val results = FilterResults()

                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = annList
                    results.count = annList.size
                } else {
                    val filter = charSequence.toString().toLowerCase()
                    val filterResultsData = ArrayList<AnnouncementInfo>()

                    for (data in annList) {
                        val title = data.title.toLowerCase()

                        if (title.toLowerCase().contains(filter)) {

                            filterResultsData.add(data)
                        }
                    }

                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }

                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                filteredList = filterResults.values as ArrayList<AnnouncementInfo>
                notifyDataSetChanged()
            }
        }
    }
}
