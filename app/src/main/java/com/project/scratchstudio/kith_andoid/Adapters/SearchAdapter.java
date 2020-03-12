package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.project.scratchstudio.kith_andoid.Holders.SearchHolder;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder> implements Filterable {

    public interface OnItemClickListener {
        void onItemClick(User item);
    }

    private List<User> searchList;
    private List<User> filteredData;

    private final OnItemClickListener listener;
    private Activity activity;

    public SearchAdapter(Activity activity, List<User> searchInfos, OnItemClickListener listener) {
        this.activity = activity;
        this.searchList = searchInfos;
        filteredData = searchInfos;
        this.listener = listener;
    }

    public void clearData() {
        searchList = null;
        filteredData = null;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout, viewGroup, false);
        return new SearchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder searchHolder, int i) {
        User searchInfo = filteredData.get(i);
        String name = searchInfo.firstName + " " + searchInfo.lastName;
        searchHolder.nName.setText(name);
        searchHolder.nPosition.setText(searchInfo.position);
        if (searchInfo.photo != null) {
            Picasso.with(activity).load(searchInfo.photo.replaceAll("@[0-9]*", ""))
                    .placeholder(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                    .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(searchHolder.nPhoto);
        }

        searchHolder.bind(searchInfo, listener);
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                if (charSequence == null || charSequence.length() == 0) {
                    results.values = searchList;
                    results.count = searchList.size();
                } else {
                    String filter = String.valueOf(charSequence).toLowerCase();
                    ArrayList<User> filterResultsData = new ArrayList<>();

                    for (User data : searchList) {
                        String searchInfo = data.firstName + data.lastName + data.position + data.description;
                        boolean contain = true;
                        for (String filt : filter.split(" ")) {
                            if (!searchInfo.toLowerCase().contains(filt)) {
                                contain = false;
                            }
                        }
                        if (contain) {
                            filterResultsData.add(data);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
