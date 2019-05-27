package com.project.scratchstudio.kith_andoid.Holders;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.project.scratchstudio.kith_andoid.Adapters.DialogAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.Model.DialogInfo;
import com.project.scratchstudio.kith_andoid.R;

public class DialogHolder extends RecyclerView.ViewHolder {

    public CustomFontTextView nRightMessage;
    public CustomFontTextView nLeftMessage;
    public ImageView nRightPhoto;
    public ImageView nLeftPhoto;
    private CardView cardView;

    public LinearLayout right;
    public LinearLayout left;

    public DialogHolder(@NonNull View itemView) {
        super(itemView);
        right = itemView.findViewById(R.id.right);
        left = itemView.findViewById(R.id.left);

        nRightMessage = itemView.findViewById(R.id.right_message);
        nRightPhoto = itemView.findViewById(R.id.right_photo);

        nLeftMessage = itemView.findViewById(R.id.left_message);
        nLeftPhoto = itemView.findViewById(R.id.left_photo);

        cardView = itemView.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        cardView.setCardElevation(0);
    }

    public void bind(final DialogInfo item, final DialogAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}
