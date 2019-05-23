package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.scratchstudio.kith_andoid.Holders.MessageDialogHolder;
import com.project.scratchstudio.kith_andoid.Model.MessageDialogInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageDialogAdapter extends RecyclerView.Adapter<MessageDialogHolder> {

    public interface OnItemClickListener {
        void onItemClick(MessageDialogInfo item);
    }

    private List<MessageDialogInfo> annList;
    private final MessageDialogAdapter.OnItemClickListener listener;
    private Activity activity;

    public MessageDialogAdapter(Activity activity, List<MessageDialogInfo> annInfos, MessageDialogAdapter.OnItemClickListener listener) {
        this.activity = activity;
        this.annList = annInfos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageDialogHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item_layout, viewGroup, false);
        return new MessageDialogHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageDialogHolder holder, int i) {
        MessageDialogInfo msgInfo = annList.get(i);
        holder.nTitle.setText(msgInfo.title);
        holder.nMessage.setText(msgInfo.message);

        if(msgInfo.url != null && !msgInfo.url.equals("null") && msgInfo.url.equals("")) {
            Picasso.with(activity).load(msgInfo.url.replaceAll("@[0-9]*", ""))
                    .transform(new PicassoCircleTransformation())
                    .into(holder.nPhoto);
        }

        holder.bind(msgInfo, listener);
    }

    @Override
    public int getItemCount() {
        return annList.size();
    }
}
