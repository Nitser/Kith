package com.project.scratchstudio.kith_andoid.Service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.project.scratchstudio.kith_andoid.Activities.CodeActivity;
import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;
import com.project.scratchstudio.kith_andoid.Fragments.TreeFragment;
import com.project.scratchstudio.kith_andoid.network.model.user.User;
import com.project.scratchstudio.kith_andoid.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TreeService {

    private int[] buttonIndex ;
    private int[] imageIndex ;
    private static long buttonCount = 0;

    private RelativeLayout addSmallTree(Context context){
       LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
       RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.tree1_layout, null);
       buttonIndex = new int[]{2, 4, 6};
       imageIndex = new int[]{1, 3, 5};
       setButtonListener(relativeLayout);
       return relativeLayout;
    }

    private RelativeLayout addMediumTree(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.tree2_layout, null);
        buttonIndex = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18};
        imageIndex = new int[]{1, 3, 5, 7, 9, 11, 13, 15, 17};
        setButtonListener(relativeLayout);
        return relativeLayout;
    }

    private RelativeLayout addBigTree(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.tree3_layout, null);
        buttonIndex = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18};
        imageIndex = new int[]{1, 3, 5, 7, 9, 11, 13, 15, 17};
        setButtonListener(relativeLayout);
        return relativeLayout;
    }

    private void setButtonListener(RelativeLayout layout){
        for(int index : buttonIndex){
            Button button = (Button) layout.getChildAt(index);
            button.setOnClickListener(view ->{
                if (SystemClock.elapsedRealtime() - buttonCount < 1000){
                    return;
                }
                buttonCount = SystemClock.elapsedRealtime();
                view.setEnabled(false);
                Intent intent = new Intent(layout.getContext(), CodeActivity.class);
                layout.getContext().startActivity(intent);
                view.setEnabled(true);

            });
        }
    }

    public void makeTree(LinearLayout parentLayout, List<User> list, boolean owner){
        HomeActivity homeActivity = (HomeActivity) parentLayout.getContext();
        int bitmapIndex=0;
        int id = 0;
        int treeIndex = 4;
        int treeCount = (list.size()-3)/9 + 8;

        while (treeIndex < treeCount){
            RelativeLayout newLayout;
            if(treeIndex == 4)
                newLayout = addSmallTree(parentLayout.getContext());
            else if(treeIndex == 5)
                newLayout = addMediumTree(parentLayout.getContext());
            else
                newLayout = addBigTree(parentLayout.getContext());
            parentLayout.addView(newLayout);

            for(int index : imageIndex){
                ImageView imageView = (ImageView) newLayout.getChildAt(index);
                if(bitmapIndex < list.size()){
                    Picasso.with(parentLayout.getContext()).load(list.get(bitmapIndex).getUrl())
                            .placeholder(R.mipmap.person)
                            .error(R.mipmap.person)
                            .transform(new PicassoCircleTransformation())
                            .into(imageView);
                    imageView.setTag(new Count(id));
                    imageView.setOnClickListener(view -> {

                        if (SystemClock.elapsedRealtime() - buttonCount < 1000){
                            return;
                        }
                        buttonCount = SystemClock.elapsedRealtime();
                            view.setEnabled(false);
                            Count count = (Count) view.getTag();
                            User user = list.get(count.getIndex());

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("another_user", true);
                            bundle.putSerializable("user", user);
                            HomeActivity.getStackBundles().add(bundle);
                            homeActivity.loadFragment(TreeFragment.newInstance(bundle));
                            view.setEnabled(true);
                    });
                    id++;
                    bitmapIndex++;
                } else if (owner){
                    newLayout.getChildAt(index + 1).setVisibility(View.VISIBLE);
                    return ;
                } else {
                    return;
                }
            }
            treeIndex++;
        }

    }

    private class Count{
        private int index;

        Count(int i){ index = i; }

        int getIndex() {
            return index;
        }
    }

}
