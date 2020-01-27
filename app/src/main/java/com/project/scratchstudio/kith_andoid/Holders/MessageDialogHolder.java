package com.project.scratchstudio.kith_andoid.Holders;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Adapters.MessageDialogAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.AnnouncementInfo;
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

    public void bind(final AnnouncementInfo item, final MessageDialogAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}
