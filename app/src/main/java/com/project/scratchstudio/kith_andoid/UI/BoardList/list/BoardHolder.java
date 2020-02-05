package com.project.scratchstudio.kith_andoid.UI.BoardList.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.CustomViews.CustomFontTextView;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.network.model.board.Board;


public class BoardHolder extends RecyclerView.ViewHolder  {

    public ImageView image;
    public View bluring;
    public CustomFontTextView title;
    public CustomFontTextView date;
    public CheckBox favorite;

    public BoardHolder(@NonNull View itemView){
        super(itemView);
        image = itemView.findViewById(R.id.photo);
        bluring = itemView.findViewById(R.id.view);
        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        favorite = itemView.findViewById(R.id.favorite);
    }

    public void bind(final Board item, final BoardAdapter.OnItemClickListener listener, int id) {
        itemView.setOnClickListener(v -> listener.onItemClick(item, id, this));
    }
}
