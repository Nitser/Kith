package com.project.scratchstudio.kith_andoid.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.project.scratchstudio.kith_andoid.R;

import java.util.ArrayList;
import java.util.List;

public class TreeService {

    private int[] buttonIndex ;
    private int[] imageIndex ;

    private static List<Bitmap> bitmaps;

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

                BitmapDrawable bitmapDrawable = ((BitmapDrawable) view.getResources().getDrawable(R.mipmap.person));
                Bitmap bitmap = bitmapDrawable .getBitmap();
                bitmaps.add(bitmap);

            });
        }
    }

    public void setAttributes(LinearLayout parentLayout){
        if(bitmaps==null)
            bitmaps = new ArrayList<>();

        int bitmapIndex=0;
        int treeIndex = 4;
        int treeCount = (bitmaps.size()-3)/9 + 8;

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
                if(bitmapIndex < bitmaps.size()){
                    imageView.setImageBitmap(bitmaps.get(bitmapIndex));
                    bitmapIndex++;
                } else {
                    newLayout.getChildAt(index + 1).setVisibility(View.VISIBLE);
                    return ;
                }
            }
            treeIndex++;
        }

    }

}
