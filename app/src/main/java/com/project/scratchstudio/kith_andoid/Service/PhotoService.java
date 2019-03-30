package com.project.scratchstudio.kith_andoid.Service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoService  {

    private Activity context;

    public PhotoService(Activity context){ this.context = context; }

    public String base64Photo(Bitmap photo) throws UnsupportedEncodingException {
        if(photo == null)
            return null;

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOS);
        byte[] result = Base64.encode(byteArrayOS.toByteArray(), Base64.NO_WRAP );
        return new String(result, "UTF-8");
    }

    public Bitmap decodeBase64Image(String encodedImage) throws UnsupportedEncodingException {
        byte[] decodedString = Base64.decode(encodedImage.getBytes("UTF-8"), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap decodingPhoto(Resources res, int id, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap resultBitmap = BitmapFactory.decodeResource(res, id, options);
        return resultBitmap;
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

    public Bitmap changePhoto(Bitmap bitmap, Uri uri){
        int angel=-1;
        String path = getRealPathFromURI(uri);
        try {
            angel = getExifAngle(path);
        } catch (Exception e){
//            Log.i("ERROR IN ANGEL:", e.getMessage());
        }
//        Log.i("ANGEL: ", String.valueOf(angel));

        bitmap = changeOrientation( bitmap, angel );
        return bitmap;
    }

    private Bitmap changeOrientation(Bitmap bitmap, int angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    private int getExifAngle(String path) {
        int angle = 0;
        int orientation=-1;
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
            ExifInterface ei = new ExifInterface(path);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {
                case 6:
                    angle = 90;
                    break;
                case 3:
                    angle = 180;
                    break;
                case 8:
                    angle = 270;
                    break;

                default:angle = 0;
                    break;
            }
        } catch (Exception e) {
//            Log.d("ANGLE ERROR", e.toString());
        }
//        Log.i("ANGEL ORIENTATION: ", String.valueOf(orientation));
        return angle;
    }

    private String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}
