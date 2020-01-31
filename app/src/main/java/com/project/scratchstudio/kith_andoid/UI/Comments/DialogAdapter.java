package com.project.scratchstudio.kith_andoid.UI.Comments;

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

public class DialogAdapter extends RecyclerView.Adapter<DialogHolder> {

    public interface OnItemClickListener {
        void onItemClick(Comment item);
    }

    private List<Comment> dialogList = new ArrayList<>();
    private DialogAdapter.OnItemClickListener listener;
    private Activity activity;

    public DialogAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setDialogList(List<Comment> list) {
        dialogList = list;
    }

    //TODO: for active comments
//    public DialogAdapter(Activity activity, List<Comment> annInfos, DialogAdapter.OnItemClickListener listener) {
//        this.activity = activity;
//        dialogList = annInfos;
//        this.listener = listener;
//    }

    @NonNull
    @Override
    public DialogHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_dialog_item_layout, viewGroup, false);
        return new DialogHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogHolder holder, int i) {
        Comment comment = dialogList.get(i);
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
        holder.bind(comment, listener);
    }

    @Override
    public int getItemCount() {
        return dialogList.size();
    }

    public long getItemId(int position) {
        return position;
    }

}
