package com.project.scratchstudio.kith_andoid.ui.home_package.home.search_user_list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.utils.PicassoCircleTransformation
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.util.ArrayList

class SearchAdapter(private val activity: FragmentActivity, private val searchList: List<UserModelView>, private val listener: OnItemClickListener) : RecyclerView.Adapter<SearchHolder>(), Filterable {
    private var filteredData: List<UserModelView>? = null

    interface OnItemClickListener {
        fun onItemClick(item: UserModelView)
    }

    init {
        filteredData = searchList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SearchHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_layout, viewGroup, false)
        return SearchHolder(itemView)
    }

    override fun onBindViewHolder(searchHolder: SearchHolder, i: Int) {
        val searchInfo = filteredData!![i]
        val name = searchInfo.firstName + " " + searchInfo.lastName
        searchHolder.nName.text = name
        searchHolder.nPosition.text = searchInfo.position
        if (searchInfo.photo != "") {
            Picasso.with(activity).load(Const.BASE_URL + searchInfo.photo)
                    .placeholder(R.mipmap.person)
                    .error(R.mipmap.person)
                    .transform(PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(searchHolder.nPhoto)
        }

        searchHolder.bind(searchInfo, listener)
    }

    override fun getItemCount(): Int {
        return filteredData!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val results = FilterResults()

                if (charSequence == null || charSequence.isEmpty()) {
                    results.values = searchList
                    results.count = searchList.size
                } else {
                    val filter = charSequence.toString().toLowerCase()
                    val filterResultsData = ArrayList<UserModelView>()

                    for (data in searchList) {
                        val searchInfo = data.firstName + data.lastName + data.position + data.description
                        var contain = true
                        for (filt in filter.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                            if (!searchInfo.toLowerCase().contains(filt)) {
                                contain = false
                            }
                        }
                        if (contain) {
                            filterResultsData.add(data)
                        }
                    }

                    results.values = filterResultsData
                    results.count = filterResultsData.size
                }

                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredData = filterResults.values as ArrayList<UserModelView>
                notifyDataSetChanged()
            }
        }
    }
}
