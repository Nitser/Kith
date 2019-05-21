package com.project.scratchstudio.kith_andoid.Holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.project.scratchstudio.kith_andoid.Adapters.AnnouncementAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
import com.project.scratchstudio.kith_andoid.R;

import jp.wasabeef.blurry.Blurry;

public class AnnouncementHolder extends RecyclerView.ViewHolder  {

    public ImageView image;
    public CustomFontTextView title;
    public CustomFontTextView date;
    private View view;

    public AnnouncementHolder(@NonNull View itemView){
        super(itemView);
        image = itemView.findViewById(R.id.photo);
        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);

        view = itemView.findViewById(R.id.view);
        RelativeLayout container = itemView.findViewById(R.id.container);
//        Blurry.with(itemView.getContext()).radius(25).sampling(2).onto(container);
    }

    public void bind(final AnnouncementInfo item, final AnnouncementAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}
