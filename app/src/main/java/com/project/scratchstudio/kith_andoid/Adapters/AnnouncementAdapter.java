package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Holders.AnnouncementHolder;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementHolder> {

    public interface OnItemClickListener {
        void onItemClick(AnnouncementInfo item);
    }

    private List<AnnouncementInfo> annList;
    private final OnItemClickListener listener;
    private Activity activity;

    public AnnouncementAdapter(Activity activity, List<AnnouncementInfo> annInfos, OnItemClickListener listener) {
        this.activity = activity;
        this.annList = annInfos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnnouncementHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_list_item_layout, viewGroup, false);
        return new AnnouncementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementHolder holder, int i) {
        AnnouncementInfo annInfo = annList.get(i);
        holder.title.setText(annInfo.title);
        holder.date.setText(annInfo.endDate.replaceAll("\\s.*$", ""));

        if(annInfo.url != null && !annInfo.url.equals("null") && !annInfo.url.equals("")) {
//            .replaceAll("@[0-9]*", "")
            Picasso.with(activity).load(annInfo.url)
                    .into(holder.image);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        holder.bind(annInfo, listener);
    }

    @Override
    public int getItemCount() {
        return annList.size();
    }

}
