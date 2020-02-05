package com.project.scratchstudio.kith_andoid.UI.BoardList.list;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.UI.BoardList.BoardsFragment;
import com.project.scratchstudio.kith_andoid.network.model.board.Board;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BoardAdapter extends RecyclerView.Adapter<BoardHolder> {

    private final OnItemClickListener listener;
    private final BoardsFragment fragment;
    private List<Board> annList = new ArrayList<>();
    private Activity activity;

    public BoardAdapter(Activity activity, OnItemClickListener listener, BoardsFragment fragment) {
        this.activity = activity;
        this.listener = listener;
        this.fragment = fragment;
    }

    public void setAnnList(List<Board> annList) {
        this.annList = annList;
    }

    public List<Board> getAnnList() {
        return annList;
    }

    @NonNull
    @Override
    public BoardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_list_item_layout, viewGroup, false);
        return new BoardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardHolder holder, int i) {
        Board board = annList.get(i);
        holder.title.setText(board.title);
        if (board.subscriptionOnBoard == 1) {
            holder.favorite.setChecked(true);
        } else {
            holder.favorite.setChecked(false);
        }

        try {
            DateFormat inputFormat;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = inputFormat.parse(board.endDate.replaceAll("\\s.*$", ""));
                String outputDateStr = outputFormat.format(date);
                holder.date.setText(outputDateStr);
            } else {
                holder.date.setText(board.endDate.replaceAll("\\s.*$", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (board.url != null && !board.url.equals("null") && !board.url.equals("")) {
            Picasso.with(activity).load(board.url)
                    .error(R.drawable.newspaper)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        holder.favorite.setOnClickListener(l -> {
            holder.favorite.setEnabled(false);
            if (board.subscriptionOnBoard != 1) {
                fragment.subscribeAnnouncement(HomeActivity.getMainUser().getId(), board, holder.favorite);
            } else {
                fragment.unsubscribeAnnouncement(HomeActivity.getMainUser().getId(), board, holder.favorite);
            }
        });

        holder.bind(board, listener, i);
    }

    @Override
    public int getItemCount() {
        return annList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(Board item, int id, BoardHolder boardHolder);
    }

}
