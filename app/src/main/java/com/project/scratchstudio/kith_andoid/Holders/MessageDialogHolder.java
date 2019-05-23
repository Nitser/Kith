package com.project.scratchstudio.kith_andoid.Holders;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Adapters.MessageDialogAdapter;
import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.MessageDialogInfo;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.R;

public class MessageDialogHolder  extends RecyclerView.ViewHolder {

    public CustomFontTextView nTitle;
    public CustomFontTextView nMessage;
    public ImageView nPhoto;
    private CardView cardView;

    public MessageDialogHolder(@NonNull View itemView) {
        super(itemView);
        nTitle = itemView.findViewById(R.id.title);
        nMessage = itemView.findViewById(R.id.message);
        nPhoto = itemView.findViewById(R.id.photo);

        cardView = itemView.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        cardView.setCardElevation(0);
    }

    public void bind(final MessageDialogInfo item, final MessageDialogAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}
