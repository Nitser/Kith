package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Holders.AnnouncementHolder;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import io.alterac.blurkit.BlurLayout;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementHolder> {

    public interface OnItemClickListener {
        void onItemClick(AnnouncementInfo item, int id);
    }

    private List<AnnouncementInfo> annList;
    private final OnItemClickListener listener;
    private Activity activity;
    private BlurLayout blurLayout;

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

        try {
            DateFormat inputFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = inputFormat.parse(annInfo.endDate.replaceAll("\\s.*$", ""));
                String outputDateStr = outputFormat.format(date);
                holder.date.setText(outputDateStr);
            } else
                holder.date.setText(annInfo.endDate.replaceAll("\\s.*$", ""));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(annInfo.url != null && !annInfo.url.equals("null") && !annInfo.url.equals("")) {
            Picasso.with(activity).load(annInfo.url)
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        holder.bind(annInfo, listener, i);
    }

    @Override
    public int getItemCount() {
        return annList.size();
    }

}
