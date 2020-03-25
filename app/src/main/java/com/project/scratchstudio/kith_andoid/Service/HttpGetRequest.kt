package com.project.scratchstudio.kith_andoid.Service

import android.app.Activity
import android.os.AsyncTask

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class HttpGetRequest : AsyncTask<String, Void, String> {

    private var code: Int = 0
    private lateinit var inputKeys: Array<String>
    private lateinit var inputData: Array<String>
    private var asyncResponse: HttpGetRequest.AsyncResponse? = null
    private var weakActivity: WeakReference<Activity>? = null

    interface AsyncResponse {
        fun processFinish(output: Boolean?, resultJSON: String, code: Int)
    }

    internal constructor(activity: Activity, asyncResponse: AsyncResponse) {
        weakActivity = WeakReference(activity)
        this.asyncResponse = asyncResponse
    }

    internal constructor(activity: Activity, input_keys: Array<String>, input_data: Array<String>, asyncResponse: AsyncResponse) {
        weakActivity = WeakReference(activity)
        this.inputKeys = input_keys
        this.inputData = input_data
        this.asyncResponse = asyncResponse
    }

    override fun doInBackground(vararg strings: String): String {
        val stringUrl = strings[0]

        var resultJSON: String
        try {
            val url = URL(stringUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = REQUEST_METHOD
            connection.readTimeout = READ_TIMEOUT
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.doInput = true
            connection.setRequestProperty(inputKeys[0], inputData[0])
            connection.connect()

            code = connection.responseCode

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val streamReader = InputStreamReader(connection.inputStream)
                val reader = BufferedReader(streamReader)
                val stringBuilder = StringBuilder()

                for(inputLine in reader.readLine()){
                    stringBuilder.append(inputLine)
                }

                reader.close()
                streamReader.close()

                resultJSON = stringBuilder.toString()
            } else {
                resultJSON = ""
            }

        } catch (e: IOException) {
            e.printStackTrace()
            resultJSON = ""
        }

        return resultJSON
    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        if (weakActivity != null && canContinue()) {
            asyncResponse!!.processFinish(true, s, code)
        }
    }

    private fun canContinue(): Boolean {
        val activity = weakActivity!!.get()
        return !(activity == null || activity.isFinishing)
    }

    companion object {
        private val REQUEST_METHOD = "GET"
        private val READ_TIMEOUT = 15000
        private val CONNECTION_TIMEOUT = 15000
    }

}