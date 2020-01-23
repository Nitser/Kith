package com.project.scratchstudio.kith_andoid.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.scratchstudio.kith_andoid.Holders.TreeHolder;
import com.project.scratchstudio.kith_andoid.network.model.User;
import com.project.scratchstudio.kith_andoid.R;
import com.project.scratchstudio.kith_andoid.Service.PicassoCircleTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TreeAdapter extends RecyclerView.Adapter<TreeHolder> {

    public interface OnItemClickListener {
        void onItemClick(User item);
    }

    private List<User> userList;
    private final TreeAdapter.OnItemClickListener listener;
    private Activity activity;

    public TreeAdapter(Activity activity, List<User> annInfos, TreeAdapter.OnItemClickListener listener) {
        this.activity = activity;
        this.userList = annInfos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TreeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout, viewGroup, false);
        return new TreeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TreeHolder holder, int i) {
        User user = userList.get(i);
        String name = user.getFirstName() + " " + user.getLastName();
        holder.name.setText(name);
        holder.position.setText(user.getPosition());

            Picasso.with(activity).load(user.getUrl().replaceAll("@[0-9]*", ""))
                    .placeholder(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                    .error(com.project.scratchstudio.kith_andoid.R.mipmap.person)
                    .transform(new PicassoCircleTransformation())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.image);

        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
