package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.project.scratchstudio.kith_andoid.Holders.MessageDialogHolder;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageDialogAdapter extends RecyclerView.Adapter<MessageDialogHolder> implements Filterable {

    public interface OnItemClickListener {
        void onItemClick(AnnouncementInfo item);
    }

    private List<AnnouncementInfo> annList;
    private List<AnnouncementInfo> filteredList;
    private final MessageDialogAdapter.OnItemClickListener listener;
    private Activity activity;

    public MessageDialogAdapter(Activity activity, List<AnnouncementInfo> annInfos, MessageDialogAdapter.OnItemClickListener listener) {
        this.activity = activity;
        this.annList = annInfos;
        filteredList = annInfos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageDialogHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_layout, viewGroup, false);
        return new MessageDialogHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDialogHolder holder, int i) {
        AnnouncementInfo msgInfo = annList.get(i);
        holder.nTitle.setText(msgInfo.title);
        holder.nMessage.setText("some msg");

        if(msgInfo.url != null && !msgInfo.url.equals("null") && !msgInfo.url.equals("")) {
            Picasso.with(activity).load(msgInfo.url.replaceAll("@[0-9]*", ""))
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.newspaper)
                    .error(R.drawable.newspaper)
                    .into(holder.nPhoto);
        }

        holder.bind(msgInfo, listener);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
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
                    results.values = annList;
                    results.count = annList.size();
                }
                else {
                    String filter = String.valueOf(charSequence).toLowerCase();
                    ArrayList<AnnouncementInfo> filterResultsData = new ArrayList<>();

                    for(AnnouncementInfo data : annList) {
                        String title = data.title.toLowerCase() ;

                        if(title.toLowerCase().contains(filter)) {

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
                filteredList = (ArrayList<AnnouncementInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
