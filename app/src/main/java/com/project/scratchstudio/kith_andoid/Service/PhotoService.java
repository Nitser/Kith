package com.project.scratchstudio.kith_andoid.Service;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoService {

    private Activity context;

    public PhotoService(Activity context) {
        this.context = context;
    }

    String base64Photo(Bitmap photo) throws UnsupportedEncodingException {
        if (photo == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOS);
        byte[] result = Base64.encode(byteArrayOS.toByteArray(), Base64.NO_WRAP);
        return new String(result, "UTF-8");
    }

    public Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public Bitmap compressPhoto(Bitmap bitmap) {
        File file = new File(context.getFilesDir(), "tmp");
        if (!file.exists()) {
            file.mkdirs();
        }
        File img = new File(file, "tmp_img");
        try {
            FileOutputStream out = new FileOutputStream(img);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
        context.deleteFile("tmp_img");
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

    public Bitmap changePhoto(Bitmap bitmap, Uri uri) {
        int angel = -1;
        String path = getRealPathFromURI(uri);
        File f = new File(path);
        try {
            angel = getExifAngle(f);
        } catch (Exception e) {
            Log.i("ERROR IN ANGEL:", e.getMessage());
        }
        bitmap = changeOrientation(f, bitmap, angel);
        return bitmap;
    }

    private Bitmap changeOrientation(File f, Bitmap bitmap, int angle) {
        Matrix mat = new Matrix();
        mat.postRotate(angle);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        try {
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);
            bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
            ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    outstudentstreamOutputStream);
        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        }

        return bitmap;
    }

    private int getExifAngle(File f) {
        int angle = 0;
        try {
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("TAG", "-- OOM Error in setting image");
        }

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

    public Bitmap decodingPhoto(Resources res, int id, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap resultBitmap = BitmapFactory.decodeResource(res, id, options);
        return resultBitmap;
    }
}
