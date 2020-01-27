package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.scratchstudio.kith_andoid.Holders.DialogHolder;
import com.project.scratchstudio.kith_andoid.Model.DialogInfo;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogHolder>  {

    public boolean newItem = false;

    public interface OnItemClickListener {
        void onItemClick(DialogInfo item);
    }

    private List<DialogInfo> dialogList;
    private final DialogAdapter.OnItemClickListener listener;
    private Activity activity;

    public DialogAdapter(Activity activity, List<DialogInfo> annInfos, DialogAdapter.OnItemClickListener listener) {
        this.activity = activity;
        dialogList = annInfos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DialogHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_dialog_item_layout, viewGroup, false);
        return new DialogHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogHolder holder, int i) {
        DialogInfo msgInfo = dialogList.get(i);
        holder.name.setText(msgInfo.user_name);
        holder.position.setText(msgInfo.position);
        holder.time.setText(msgInfo.date.replaceAll("[-0-9]*\\s", "").replaceAll(":[0-9]*$", ""));

        try {
            DateFormat inputFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date newDateFormat = null;
                newDateFormat = inputFormat.parse(msgInfo.date.replaceAll("\\s.*$", ""));
                String outputDateStr = outputFormat.format(newDateFormat);
                holder.date.setText(outputDateStr);
            } else
                holder.date.setText(msgInfo.date.replaceAll("\\s.*$", ""));

        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.comment.setText(msgInfo.message);
        Picasso.with(activity).load(msgInfo.photo.replaceAll("@[0-9]*", ""))
                        .transform(new PicassoCircleTransformation())
                        .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(holder.photo);

//        if(msgInfo.user_id == HomeActivity.getMainUser().getId()){
//            holder.right.setVisibility(View.VISIBLE);
//            holder.left.setVisibility(View.INVISIBLE);
//            holder.nRightMessage.setText(msgInfo.message);
//            if(msgInfo.photo != null && !msgInfo.photo.equals("null") && !msgInfo.photo.equals("")) {
//                Picasso.with(activity).load(msgInfo.photo.replaceAll("@[0-9]*", ""))
//                        .transform(new PicassoCircleTransformation())
//                        .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                        .into(holder.nRightPhoto);
//            }
//        } else {
//            holder.right.setVisibility(View.INVISIBLE);
//            holder.left.setVisibility(View.VISIBLE);
//            holder.nLeftMessage.setText(msgInfo.message);
//            if(msgInfo.photo != null && !msgInfo.photo.equals("null") && !msgInfo.photo.equals("")) {
//                Picasso.with(activity).load(msgInfo.photo.replaceAll("@[0-9]*", ""))
//                        .transform(new PicassoCircleTransformation())
//                        .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                        .into(holder.nLeftPhoto);
//            }
//        }
        holder.bind(msgInfo, listener);
    }

    @Override
    public int getItemCount() {
        return dialogList.size();
    }

    public long getItemId(int position)
    {
        return position;
    }

}
