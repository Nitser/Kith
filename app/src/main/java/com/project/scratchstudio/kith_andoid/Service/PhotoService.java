package com.project.scratchstudio.kith_andoid.Service;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoService  {

    private Context context;

    public PhotoService(Context context){ this.context = context; }

    public Bitmap decodingPhoto(Resources res, int id, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap resultBitmap = BitmapFactory.decodeResource(res, id, options);

//        resultBitmap = changeOrientation( resultBitmap, getExifAngle(path) );
        return resultBitmap;
    }

    public Bitmap decodingPhoto(Bitmap bitmap, Uri uri){
        bitmap = changeOrientation( bitmap, getExifAngle(uri.getPath()) );
        return bitmap;
    }


    public Bitmap compressPhoto(Bitmap bitmap, String path) {

        try(FileOutputStream out = new FileOutputStream(path)){

            bitmap.compress(Bitmap.CompressFormat.JPEG,20,out);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    Bitmap changeOrientation(Bitmap bitmap, int angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    private static int getExifAngle(String path) {
        int angle = 0;
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;

                default:angle = 0;
                    break;
            }
        } catch (Exception e) {
            Log.d("!!!!!!_ANGLE", e.toString());
        }
        return angle;
    }

}
