package com.project.scratchstudio.kith_andoid.Service;

import android.app.Activity;
import android.os.AsyncTask;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    private int code;
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private String[] input_keys;
    private String[] input_data;
    private HttpGetRequest.AsynResponse asynResponse;
    private WeakReference<Activity> weakActivity;

    public interface AsynResponse {
        void processFinish(Boolean output, String resultJSON, int code);
    }

    HttpGetRequest(Activity activity, AsynResponse asynResponse){
        weakActivity = new WeakReference<>(activity);
        this.asynResponse = asynResponse;
    }

    HttpGetRequest(Activity activity, String[] input_keys, String[] input_data, AsynResponse asynResponse){
        weakActivity = new WeakReference<>(activity);
        this.input_keys = input_keys;
        this.input_data = input_data;
        this.asynResponse = asynResponse;
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = strings[0];
        String inputLine;

        String resultJSON;
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            if(input_keys != null){
                connection.setDoInput(true);
                connection.setRequestProperty(input_keys[0], input_data[0]);
            }
            connection.connect();

            code = connection.getResponseCode();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                reader.close();
                streamReader.close();

                resultJSON = stringBuilder.toString();
            } else {
                resultJSON = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            resultJSON = null;
        }

        return resultJSON;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(weakActivity != null && canContinue()) {
            asynResponse.processFinish(true, s, code);
        }
    }

    private boolean canContinue() {
        Activity activity = weakActivity.get();
        return activity != null && !activity.isFinishing() ;
    }

}