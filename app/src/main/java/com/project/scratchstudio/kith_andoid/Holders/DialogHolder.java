package com.project.scratchstudio.kith_andoid.Holders;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Adapters.DialogAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DialogHolder extends RecyclerView.ViewHolder {

    public CustomFontTextView name;
    public CustomFontTextView position;
    public CustomFontTextView time;
    public CustomFontTextView date;
    public CustomFontTextView comment;
    public ImageView photo;
    private CardView cardView;

    public DialogHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        position = itemView.findViewById(R.id.position);
        time = itemView.findViewById(R.id.time);
        date = itemView.findViewById(R.id.date);
        comment = itemView.findViewById(R.id.comment);

        photo = itemView.findViewById(R.id.photo);

        cardView = itemView.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(Color.parseColor("#eff1f2"));
        cardView.setCardElevation(0);
    }

    public void bind(final Comment item, final DialogAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}
