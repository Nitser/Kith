package com.project.scratchstudio.kith_andoid.Holders;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Adapters.TreeAdapter;
import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.R;

public class TreeHolder extends RecyclerView.ViewHolder  {

    public ImageView image;
    public CustomFontTextView name;
    public CustomFontTextView position;
    private CardView cardView;

    public TreeHolder(@NonNull View itemView){
        super(itemView);
        image = itemView.findViewById(R.id.photo);
        name = itemView.findViewById(R.id.name);
        position = itemView.findViewById(R.id.position);

        cardView = itemView.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        cardView.setCardElevation(0);

    }

    public void bind(final User item, final TreeAdapter.OnItemClickListener listener) {
        itemView.setOnClickListener(v -> listener.onItemClick(item));
    }
}
