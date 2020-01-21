package com.project.scratchstudio.kith_andoid.Holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Adapters.AnnouncementAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;


public class AnnouncementHolder extends RecyclerView.ViewHolder  {

    public ImageView image;
    public View bluring;
    public CustomFontTextView title;
    public CustomFontTextView date;
    public ImageView favorite;

    public AnnouncementHolder(@NonNull View itemView){
        super(itemView);
        image = itemView.findViewById(R.id.photo);
        bluring = itemView.findViewById(R.id.view);
        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        favorite = itemView.findViewById(R.id.favorite);
    }

    public void bind(final AnnouncementInfo item, final AnnouncementAdapter.OnItemClickListener listener, int id) {
        itemView.setOnClickListener(v -> listener.onItemClick(item, id));
    }
}
