package com.project.scratchstudio.kith_andoid.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import com.project.scratchstudio.kith_andoid.model.PhotoModelView

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Calendar

class PhotoService(private val context: Context) {

    fun fullPreparingPhoto(bitmap: Bitmap, uri: Uri): PhotoModelView? {
        val newPhoto = PhotoModelView()
        val newBitmap: Bitmap
        val path = getRealPathFromURI(uri)
        val f = File(path!!)

        newBitmap = changeOrientation(f, bitmap, getExifAngle(f))

        val file = File(context.filesDir, "kith")
        if (!file.exists()) {
            file.mkdirs()
        }
        val img = File(file, "kith_img${Calendar.getInstance().time}")
        val out = FileOutputStream(img)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)

        newPhoto.photoBitmap = newBitmap
        newPhoto.photoPhoneStoragePath = img.path
        newPhoto.phoneStorageFile = img

        return newPhoto
    }

    fun compressPhoto(bitmap: Bitmap): Bitmap {
        val file = File(context.filesDir, "tmp")
        if (!file.exists()) {
            file.mkdirs()
        }
        val img = File(file, "tmp_img")
        try {
            val out = FileOutputStream(img)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        context.deleteFile("tmp_img")
        return bitmap
    }

    fun changePhoto(bitmap: Bitmap, uri: Uri): Bitmap {
        var angel = -1
        val path = getRealPathFromURI(uri)
        val f = File(path)
        try {
            angel = getExifAngle(f)
        } catch (e: Exception) {
            Log.i("ERROR IN ANGEL:", e.message)
        }
        return changeOrientation(f, bitmap, angel)
    }

    fun changePhoto(bitmap: Bitmap, file: File): Bitmap {
        var angel = -1
        try {
            angel = getExifAngle(file)
        } catch (e: Exception) {
            Log.i("ERROR IN ANGEL:", e.message)
        }
        return changeOrientation(file, bitmap, angel)
    }

    private fun changeOrientation(f: File, bitmap: Bitmap, angle: Int): Bitmap {
        var newBitmap = bitmap
        val mat = Matrix()
        mat.postRotate(angle.toFloat())
        val options = BitmapFactory.Options()
        options.inSampleSize = 2

        try {
            val bmp = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            newBitmap = Bitmap.createBitmap(bmp!!, 0, 0, bmp.width,
                    bmp.height, mat, true)
            val outstudentstreamOutputStream = ByteArrayOutputStream()
            newBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    outstudentstreamOutputStream)
        } catch (e: IOException) {
            Log.w("TAG", "-- Error in setting image")
        }

        return newBitmap
    }

    private fun getExifAngle(f: File): Int {
        var angle = 0
        try {
            val exif = ExifInterface(f.path)
            val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270
            }
        } catch (e: IOException) {
            Log.w("TAG", "-- Error in setting image")
        } catch (oom: OutOfMemoryError) {
            Log.w("TAG", "-- OOM Error in setting image")
        }

        return angle
    }

    fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    @Throws(UnsupportedEncodingException::class)
    fun base64Photo(photo: Bitmap?): String {
        if (photo == null) {
            return ""
        }

        val byteArrayOS = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOS)
        val result = Base64.encode(byteArrayOS.toByteArray(), Base64.NO_WRAP)
        return String(result, UTF_8)
    }

    fun resizeBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    @Throws(UnsupportedEncodingException::class)
    fun decodeBase64Image(encodedImage: String): Bitmap {
        val decodedString = Base64.decode(encodedImage.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            return null
        }

    }

    fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun decodingPhoto(res: Resources, id: Int, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, id, options)
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, id, options)
    }
}
