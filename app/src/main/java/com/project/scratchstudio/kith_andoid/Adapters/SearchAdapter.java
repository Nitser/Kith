package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Holders.SearchHolder;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder> implements Filterable {

    public interface OnItemClickListener {
        void onItemClick(SearchInfo item);
    }

    private List<SearchInfo> searchList;
    private List<SearchInfo> filteredData;

    private final OnItemClickListener listener;
    private Activity activity;

    public SearchAdapter(Activity activity, List<SearchInfo> searchInfos, OnItemClickListener listener) {
        this.activity = activity;
        this.searchList = searchInfos;
        filteredData = searchInfos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout, viewGroup, false);
        return new SearchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder searchHolder, int i) {
        SearchInfo searchInfo = filteredData.get(i);
        String name = searchInfo.firstName + " " + searchInfo.lastName;
        searchHolder.nName.setText(name);
        searchHolder.nPosition.setText(searchInfo.position);
        Picasso.with(activity).load(searchInfo.photo.replaceAll("@[0-9]*", ""))
                .placeholder(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                .transform(new PicassoCircleTransformation())
                .into(searchHolder.nPhoto);

        searchHolder.bind(searchInfo, listener);
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    //This should return a data object, not an int
    public Object getItem(int position)
    {
        return filteredData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }



    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new FilterResults();

                if(charSequence == null || charSequence.length() == 0) {
                    results.values = searchList;
                    results.count = searchList.size();
                }
                else {
                    String filter = String.valueOf(charSequence).toLowerCase().replaceAll("\\s+", "");
                    ArrayList<SearchInfo> filterResultsData = new ArrayList<>();

                    for(SearchInfo data : searchList) {
                        String lfp = data.lastName.toLowerCase() + data.firstName.toLowerCase() + data.position.toLowerCase();
                        String lpf = data.lastName.toLowerCase() + data.position.toLowerCase() + data.firstName.toLowerCase();
                        String flp = data.firstName.toLowerCase() + data.lastName.toLowerCase() + data.position.toLowerCase();
                        String fpl = data.firstName.toLowerCase() + data.position.toLowerCase() + data.lastName.toLowerCase();
                        String plf = data.position.toLowerCase() + data.lastName.toLowerCase() + data.firstName.toLowerCase();
                        String pfl = data.position.toLowerCase() + data.firstName.toLowerCase() + data.lastName.toLowerCase();

                        if(flp.toLowerCase().contains(filter) || lpf.toLowerCase().contains(filter) || lfp.toLowerCase().contains(filter)
                                || fpl.toLowerCase().contains(filter) || plf.toLowerCase().contains(filter) || pfl.toLowerCase().contains(filter)
                                || data.lastName.toLowerCase().contains(filter) || data.firstName.toLowerCase().contains(filter) || data.position.toLowerCase().contains(filter)) {

                            filterResultsData.add(data);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                filteredData = (ArrayList<SearchInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
