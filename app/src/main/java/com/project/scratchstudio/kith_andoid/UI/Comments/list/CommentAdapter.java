package com.project.scratchstudio.kith_andoid.UI.Comments.list;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.project.scratchstudio.kith_andoid.network.model.comment.Comment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {

    public interface OnItemClickListener {
        void onItemClick(Comment item);
    }

    private List<Comment> commentList = new ArrayList<>();
    private Activity activity;

    public CommentAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setCommentList(List<Comment> list) {
        commentList = list;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_dialog_item_layout, viewGroup, false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int i) {
        Comment comment = commentList.get(i);
        holder.name.setText(comment.getUser().lastName + " " + comment.getUser().firstName);
        holder.position.setText(comment.getUser().position);
        holder.time.setText(comment.getTimestamp().replaceAll("[-0-9]*\\s", "").replaceAll(":[0-9]*$", ""));

        try {
            DateFormat inputFormat;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date newDateFormat;
                newDateFormat = inputFormat.parse(comment.getTimestamp().replaceAll("\\s.*$", ""));
                String outputDateStr = outputFormat.format(newDateFormat);
                holder.date.setText(outputDateStr);
            } else {
                holder.date.setText(comment.getTimestamp().replaceAll("\\s.*$", ""));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.comment.setText(comment.getMessage());
        Picasso.with(activity).load(comment.getUser().photo.replaceAll("@[0-9]*", ""))
                .transform(new PicassoCircleTransformation())
                .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

}
