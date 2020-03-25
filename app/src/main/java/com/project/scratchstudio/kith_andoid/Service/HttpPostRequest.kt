package com.project.scratchstudio.kith_andoid.Service

import android.app.Activity
import android.os.AsyncTask
import android.util.Log

import org.json.JSONObject

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

import javax.net.ssl.HttpsURLConnection

class HttpPostRequest : AsyncTask<String, Void, String> {

    private var code: Int = 0
    private lateinit var photo: String
    private lateinit var headerKeys: Array<String>
    private var bodyKeys: Array<String>
    private lateinit var headerData: Array<String>
    private var bodyData: Array<String>
    private var asyncResponse: AsyncResponse
    private var weakActivity: WeakReference<Activity>

    interface AsyncResponse {
        fun processFinish(output: Boolean?, resultJSON: String, code: Int)
    }

    internal constructor(activity: Activity, body_keys: Array<String>, body_data: Array<String>, asyncResponse: AsyncResponse) {
        weakActivity = WeakReference(activity)
        this.bodyKeys = body_keys
        this.bodyData = body_data
        this.asyncResponse = asyncResponse
    }

    internal constructor(activity: Activity, body_keys: Array<String>, body_data: Array<String>, photo: String, asyncResponse: AsyncResponse) {
        weakActivity = WeakReference(activity)
        this.photo = photo
        this.bodyKeys = body_keys
        this.bodyData = body_data
        this.asyncResponse = asyncResponse
    }

    internal constructor(activity: Activity, header_keys: Array<String>, body_keys: Array<String>, header_data: Array<String>, body_data: Array<String>, asyncResponse: AsyncResponse) {
        weakActivity = WeakReference(activity)
        this.headerKeys = header_keys
        this.bodyKeys = body_keys
        this.headerData = header_data
        this.bodyData = body_data
        this.asyncResponse = asyncResponse
    }

    override fun doInBackground(vararg strings: String): String {
        val stringUrl = strings[0]
        val postDataParams: JSONObject

        try {
            val url = URL(stringUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = REQUEST_METHOD
            connection.readTimeout = READ_TIMEOUT
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.doInput = true
            connection.doOutput = true

            for (i in headerKeys.indices) {
                connection.setRequestProperty(headerKeys[i], headerData[i])
            }


            postDataParams = JSONObject()

            postDataParams.put("photo", photo)

            for (i in bodyKeys.indices) {
                postDataParams.put(bodyKeys[i], bodyData[i])
            }

            try {
                connection.connect()
            } catch (e: Exception) {
                code = connection.responseCode
                return ""
            }

            val os = connection.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(getPostDataString(postDataParams))

            writer.flush()
            writer.close()
            os.close()

            code = connection.responseCode

            if (connection.responseCode == HttpsURLConnection.HTTP_OK) {
                Log.i("HTTP con", "ok")

                val `in` = BufferedReader(InputStreamReader(connection.inputStream))
                val sb = StringBuffer("")

                for (line in `in`.readLine()) {
                    sb.append(line)
//                    break
                }

                `in`.close()
                return sb.toString()

            } else if (connection.responseCode == 405) {
                val `in` = BufferedReader(InputStreamReader(connection.errorStream))
                val sb = StringBuffer("")

                for (line in `in`.readLine()) {
                    sb.append(line)
//                    break
                }

                `in`.close()
                return sb.toString()
            } else {
                val `in` = BufferedReader(InputStreamReader(connection.errorStream))
                val sb = StringBuffer()

                for (line in `in`.readLine()) {
                    sb.append(line)
//                    break
                }

                Log.i("CErr: ", connection.responseCode.toString())

                `in`.close()
                return ""
            }


        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        if (canContinue()) {
            asyncResponse.processFinish(true, s, code)
        }
    }

    private fun canContinue(): Boolean {
        val activity = weakActivity.get()
        return activity != null && !activity.isFinishing
    }

    @Throws(Exception::class)
    private fun getPostDataString(params: JSONObject): String {

        val result = StringBuilder()
        var first = true
        val itr = params.keys()

        while (itr.hasNext()) {

            val key = itr.next()
            val value = params.get(key)

            if (first)
                first = false
            else
                result.append("&")

            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))

        }
        return result.toString()
    }

    companion object {
        private val REQUEST_METHOD = "POST"
        private val READ_TIMEOUT = 15000
        private val CONNECTION_TIMEOUT = 15000
    }

}
