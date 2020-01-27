package com.project.scratchstudio.kith_andoid.Holders;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Adapters.SearchAdapter;
import com.project.scratchstudio.kith_andoid.Model.SearchInfo;
import com.project.scratchstudio.kith_andoid.R;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;

public class SearchHolder extends RecyclerView.ViewHolder {

    public CustomFontTextView nName;
    public CustomFontTextView nPosition;
    public ImageView nPhoto;
    private CardView cardView;

    public SearchHolder(@NonNull View itemView) {
        super(itemView);
        nName = itemView.findViewById(R.id.name);
        nPosition = itemView.findViewById(R.id.position);
        nPhoto = itemView.findViewById(R.id.photo);

        cardView = itemView.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        cardView.setCardElevation(0);
    }

    public void bind(final SearchInfo item, final SearchAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}
